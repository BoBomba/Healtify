import React, { useEffect, useMemo, useState } from 'react';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import Nav from '../../Components/Nav';
import AddAppointmentModal from '../../Components/AddAppointmentModal';
import SearchBar from '../../Components/SearchBar';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { DeleteAppointment, GetAppointments, GetMyPatients } from '../../service/doctorService';
import { formatAppointmentTime, groupAppointmentsByDay } from '../../utils/appointmentUtils';
import { MONTH_NAMES } from '../../utils/calendarUtils';
import { matchesDateRange, matchesQuery } from '../../utils/searchUtils';

// Ładny nagłówek dnia: "7 sierpnia 2026" z klucza "2026-08-07".
const formatDayHeading = (dayKey) => {
    const [year, month, day] = dayKey.split('-');
    return `${Number(day)} ${MONTH_NAMES[Number(month) - 1].toLowerCase()} ${year}`;
};

const SCOPE_TITLES = {
    upcoming: 'Wszystkie przyszłe wizyty',
    past: 'Minione wizyty',
    all: 'Wszystkie wizyty',
};

const EMPTY_FILTERS = { scope: 'upcoming', dateFrom: '', dateTo: '', patientId: '' };

// Lista jest ucinana - reszta dochodzi przyciskiem "Pokaż więcej".
const PAGE_SIZE = 10;

/**
 * Zakładka "Wizyty" u lekarza.
 * Domyślnie pokazuje tylko przyszłe wizyty, pogrupowane w dni, żeby ładnie wyglądały.
 * filtr zakresu sięga też do wizyt minionych i wszystkich razem.
 */
function DoctorData() {
    const { doctor } = useDoctorGuard();
    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    // Wizyta otwarta do edycji - ten sam modal tylko z wypełnionymi polami.
    const [editingAppointment, setEditingAppointment] = useState(null);
    const [loadError, setLoadError] = useState('');
    const [query, setQuery] = useState('');
    const [filters, setFilters] = useState(EMPTY_FILTERS);
    const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);

    const { scope } = filters;
    // Backend zna tylko "upcoming" i "all" - minione odsiewamy już u siebie,
    // więc przełączenie "Minione"/"Wszystkie" nie strzela ponownie po dane.
    const fetchScope = scope === 'upcoming' ? 'upcoming' : 'all';

    const setFilter = (name, value) => setFilters((prev) => ({ ...prev, [name]: value }));

    const activeFilterCount = Object.keys(EMPTY_FILTERS)
        .filter((name) => filters[name] !== EMPTY_FILTERS[name]).length;

    const handleReset = () => {
        setQuery('');
        setFilters(EMPTY_FILTERS);
    };

    useEffect(() => {
        if (!doctor) return;

        GetAppointments(fetchScope)
            .then((data) => setAppointments(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wizyt.');
            });
    }, [doctor, fetchScope]);

    useEffect(() => {
        if (!doctor) return;

        // Lista pacjentów jest potrzebna modalowi edycji i filtrowi po pacjencie 
        GetMyPatients()
            .then((data) => setPatients(data))
            .catch((error) => console.log(error));
    }, [doctor]);

    // Zmiana wyszukiwania zaczyna listę od nowa - inaczej po zawężeniu licznik by sie dublował z poprzednich wyników.
    useEffect(() => setVisibleCount(PAGE_SIZE), [query, filters]);

    /**
     * np. Przy widoku przyszłych wizyt przesunięcie wizyty w przeszłość usuwa ją z listy
     */
    const handleAppointmentSaved = (saved) => {
        setAppointments((prev) => {
            const updated = prev
                .map((item) => (item.appointmentId === saved.appointmentId ? saved : item))
                .filter((item) => scope !== 'upcoming' || new Date(item.appointmentAt) >= new Date());
            return updated.sort((a, b) => a.appointmentAt.localeCompare(b.appointmentAt));
        });
    };

    const filtered = useMemo(() => {
        const now = new Date();
        const result = appointments.filter((appointment) => {
            if (scope === 'past' && new Date(appointment.appointmentAt) >= now) return false;
            // Data leci też do frazy, żeby dało się wpisać "2026-08" bez filtrów.
            const matchesText = matchesQuery(
                query,
                appointment.title,
                appointment.notes,
                appointment.patient.username,
                appointment.patient.email,
                appointment.appointmentAt.slice(0, 16).replace('T', ' ')
            );
            if (!matchesText) return false;
            if (!matchesDateRange(appointment.appointmentAt, filters.dateFrom, filters.dateTo)) return false;
            if (filters.patientId !== '' && String(appointment.patient.userId) !== filters.patientId) return false;
            return true;
        });

        // PAST czyta się od najświeższych, FUTURE od najbliższych.
        return result.sort((a, b) => (scope === 'past'
            ? b.appointmentAt.localeCompare(a.appointmentAt)
            : a.appointmentAt.localeCompare(b.appointmentAt)));
    }, [appointments, query, filters, scope]);

    const visible = filtered.slice(0, visibleCount);

    const days = useMemo(() => {
        const grouped = groupAppointmentsByDay(visible);
        const dayKeys = Object.keys(grouped).sort();
        if (scope === 'past') dayKeys.reverse();
        return dayKeys.map((dayKey) => ({ dayKey, items: grouped[dayKey] }));
    }, [visible, scope]);

    const handleDelete = async (appointment) => {
        const confirmed = window.confirm(
            `Odwołać wizytę "${appointment.title}" z pacjentem ${appointment.patient.username}?`
        );
        if (!confirmed) return;

        try {
            await DeleteAppointment(appointment.appointmentId);
            setAppointments((prev) =>
                prev.filter((item) => item.appointmentId !== appointment.appointmentId)
            );
        } catch (error) {
            console.log(error);
            setLoadError('Nie udało się odwołać wizyty.');
        }
    };

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="doctor-page">
                    <h2>{SCOPE_TITLES[scope]}</h2>

                    {loadError && <div id="messages">{loadError}</div>}

                    <div className="datablock doctor-panel">
                        <SearchBar
                            query={query}
                            onQueryChange={setQuery}
                            placeholder="Szukaj po tytule, notatce, pacjencie lub dacie..."
                            onReset={handleReset}
                            activeFilterCount={activeFilterCount}
                            summary={`Wizyty: ${visible.length} z ${filtered.length}`}
                        >
                            <div className="search-field">
                                <label className="field-label" htmlFor="appointment-scope">Zakres</label>
                                <select
                                    id="appointment-scope"
                                    value={filters.scope}
                                    onChange={(e) => setFilter('scope', e.target.value)}
                                >
                                    <option value="upcoming">Przyszłe</option>
                                    <option value="past">Minione</option>
                                    <option value="all">Wszystkie</option>
                                </select>
                            </div>
                            <div className="search-field">
                                <label className="field-label" htmlFor="appointment-date-from">Od dnia</label>
                                <input
                                    id="appointment-date-from"
                                    type="date"
                                    value={filters.dateFrom}
                                    onChange={(e) => setFilter('dateFrom', e.target.value)}
                                />
                            </div>
                            <div className="search-field">
                                <label className="field-label" htmlFor="appointment-date-to">Do dnia</label>
                                <input
                                    id="appointment-date-to"
                                    type="date"
                                    value={filters.dateTo}
                                    onChange={(e) => setFilter('dateTo', e.target.value)}
                                />
                            </div>
                            <div className="search-field">
                                <label className="field-label" htmlFor="appointment-patient">Pacjent</label>
                                <select
                                    id="appointment-patient"
                                    value={filters.patientId}
                                    onChange={(e) => setFilter('patientId', e.target.value)}
                                >
                                    <option value="">Wszyscy</option>
                                    {patients.map((patient) => (
                                        <option value={patient.userId} key={patient.userId}>
                                            {patient.fullName || patient.username}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </SearchBar>

                        {filtered.length === 0 && (
                            <p>
                                {query === '' && activeFilterCount === 0
                                    ? 'Brak zaplanowanych wizyt.'
                                    : 'Brak wizyt pasujących do wyszukiwania.'}
                            </p>
                        )}

                        {days.map(({ dayKey, items }) => (
                            <div className="doctor-day-group" key={dayKey}>
                                <h3 className="doctor-day-heading">{formatDayHeading(dayKey)}</h3>
                                {items.map((appointment) => (
                                    <div className="day-event-item" key={appointment.appointmentId}>
                                        <div className="day-event-title">
                                            <span className="legend-dot appointment" />
                                            <strong>{appointment.title}</strong>
                                            <span className="day-event-time">
                                                {formatAppointmentTime(appointment.appointmentAt)}
                                            </span>
                                            <div className="day-event-actions">
                                                <button
                                                    type="button"
                                                    className="modal-btn secondary small"
                                                    onClick={() => setEditingAppointment(appointment)}
                                                >
                                                    Edytuj
                                                </button>
                                                <button
                                                    type="button"
                                                    className="modal-btn danger small"
                                                    onClick={() => handleDelete(appointment)}
                                                >
                                                    Odwołaj
                                                </button>
                                            </div>
                                        </div>
                                        <p>Pacjent: {appointment.patient.username} ({appointment.patient.email})</p>
                                        {appointment.notes && <p>{appointment.notes}</p>}
                                    </div>
                                ))}
                            </div>
                        ))}

                        {filtered.length > visible.length && (
                            <button
                                type="button"
                                className="modal-btn secondary small search-more"
                                onClick={() => setVisibleCount((prev) => prev + PAGE_SIZE)}
                            >
                                Pokaż więcej ({filtered.length - visible.length})
                            </button>
                        )}
                    </div>
                </div>
            </main>

            {/* Ta strona tylko edytuje - nowe wizyty zakłada się z kalendarza. */}
            <AddAppointmentModal
                isOpen={editingAppointment !== null}
                onClose={() => setEditingAppointment(null)}
                onSaved={handleAppointmentSaved}
                patients={patients}
                appointment={editingAppointment}
            />
        </div>
    );
}

export default DoctorData;
