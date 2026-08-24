import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Nav from '../../Components/Nav';
import SearchBar from '../../Components/SearchBar';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { GetMyPatients, GetPatientJournal } from '../../service/doctorService';
import { moodClass } from '../../utils/calendarUtils';
import { matchesDateRange, matchesQuery } from '../../utils/searchUtils';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import '../../css/profile.css';

const SCALE_FACES = ['😞', '🙁', '😐', '🙂', '😄'];

const EMPTY_FILTERS = { dateFrom: '', dateTo: '', mood: '' };

// Lista jest ucinana - reszta dochodzi przyciskiem "Pokaż więcej".
const PAGE_SIZE = 10;

/**
 * Wpisy z dziennika, udostępnione lekarzowi.
 * Widok celowo ten sam co u pacjenta w Dzienniku tylko bez przycisków
 * i bez filtra udostępnień
 */
function DoctorPatientJournal() {
    const { patientId } = useParams();
    const navigate = useNavigate();
    const { checking } = useDoctorGuard();

    const [patient, setPatient] = useState(null);
    const [entries, setEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [query, setQuery] = useState('');
    const [filters, setFilters] = useState(EMPTY_FILTERS);
    const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);

    const setFilter = (name, value) => setFilters((prev) => ({ ...prev, [name]: value }));

    const activeFilterCount = Object.keys(EMPTY_FILTERS)
        .filter((name) => filters[name] !== EMPTY_FILTERS[name]).length;

    const handleReset = () => {
        setQuery('');
        setFilters(EMPTY_FILTERS);
    };

    useEffect(() => {
        if (checking) return;

        // Nazwe pacjenta bierzemy z listy pacjentów -
        // ta i tak jest ograniczona do powiązań ACCEPTED.
        Promise.all([GetPatientJournal(patientId), GetMyPatients()])
            .then(([fetchedEntries, patients]) => {
                setEntries(fetchedEntries);
                setPatient(patients.find((item) => String(item.userId) === String(patientId)) ?? null);
            })
            .catch((err) => {
                console.log(err);
                setError(
                    err.response?.status === 403
                        ? 'Ten pacjent nie jest już pod Twoją opieką.'
                        : 'Nie udało się pobrać wpisów.'
                );
            })
            .finally(() => setLoading(false));
    }, [checking, patientId]);

    // Zmiana wyszukiwania zaczyna listę od nowa inaczej się buguje i dubluje licznik z poprzednich wyników.
    useEffect(() => setVisibleCount(PAGE_SIZE), [query, filters]);

    const filtered = useMemo(() => entries.filter((entry) => {
        // Data leci też do frazy, żeby dało się wpisać "2026-08" bez filtrów.
        const matchesText = matchesQuery(
            query,
            entry.title,
            entry.description,
            entry.symptoms ?? [],
            entry.entryAt.slice(0, 16).replace('T', ' ')
        );
        if (!matchesText) return false;
        if (!matchesDateRange(entry.entryAt, filters.dateFrom, filters.dateTo)) return false;
        if (filters.mood !== '' && String(entry.moodScale) !== filters.mood) return false;
        return true;
    }), [entries, query, filters]);

    const visible = filtered.slice(0, visibleCount);

    if (checking) {
        return (
            <div className="dashboard">
                <Nav />
                <main><p>Sprawdzanie uprawnień...</p></main>
            </div>
        );
    }

    const patientLabel = patient ? (patient.fullName || patient.username) : 'pacjenta';

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="block-container">
                    <div className="block-row">
                        {/* Ta sama klasa co w Dzienniku pacjenta */}
                        <div className="datablock calendar-day-panel">
                            <h2 className="profile-title">Wpisy - {patientLabel}</h2>

                            {loading && <p>Wczytywanie...</p>}
                            {error && <div id="messages">{error}</div>}

                            {!loading && !error && entries.length > 0 && (
                                <SearchBar
                                    query={query}
                                    onQueryChange={setQuery}
                                    placeholder="Szukaj w tytule, opisie, objawach lub dacie..."
                                    onReset={handleReset}
                                    activeFilterCount={activeFilterCount}
                                    summary={`Wpisy: ${visible.length} z ${filtered.length}${filtered.length !== entries.length ? ` (wszystkich: ${entries.length})` : ''}`}
                                >
                                    <div className="search-field">
                                        <label className="field-label" htmlFor="shared-date-from">Od dnia</label>
                                        <input
                                            id="shared-date-from"
                                            type="date"
                                            value={filters.dateFrom}
                                            onChange={(e) => setFilter('dateFrom', e.target.value)}
                                        />
                                    </div>
                                    <div className="search-field">
                                        <label className="field-label" htmlFor="shared-date-to">Do dnia</label>
                                        <input
                                            id="shared-date-to"
                                            type="date"
                                            value={filters.dateTo}
                                            onChange={(e) => setFilter('dateTo', e.target.value)}
                                        />
                                    </div>
                                    <div className="search-field">
                                        <label className="field-label" htmlFor="shared-mood">Samopoczucie</label>
                                        <select
                                            id="shared-mood"
                                            value={filters.mood}
                                            onChange={(e) => setFilter('mood', e.target.value)}
                                        >
                                            <option value="">Dowolne</option>
                                            {SCALE_FACES.map((face, index) => (
                                                <option value={index + 1} key={index}>{face} {index + 1}/5</option>
                                            ))}
                                        </select>
                                    </div>
                                </SearchBar>
                            )}

                            {!loading && !error && entries.length === 0 && (
                                <p>Ten pacjent nie udostępnił Ci jeszcze żadnych wpisów.</p>
                            )}
                            {!loading && !error && entries.length > 0 && filtered.length === 0 && (
                                <p>Brak wpisów pasujących do wyszukiwania.</p>
                            )}

                            {visible.map((entry) => (
                                <div className="day-event-item" key={entry.entryId}>
                                    <div className="day-event-title">
                                        <span className={`legend-dot ${moodClass(entry.moodScale)}`} />
                                        <strong>{entry.title}</strong>
                                        <span className="day-event-time">
                                            {entry.entryAt.slice(0, 16).replace('T', ' ')}
                                        </span>
                                    </div>
                                    <p>Samopoczucie: {entry.moodScale}/5</p>
                                    {entry.symptoms && entry.symptoms.length > 0 && (
                                        <div className="tag-list">
                                            {entry.symptoms.map((symptom, i) => (
                                                <span className={`tag-chip ${moodClass(entry.moodScale)}`} key={i}>
                                                    {symptom}
                                                </span>
                                            ))}
                                        </div>
                                    )}
                                    {entry.description && <p>{entry.description}</p>}
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

                            <button
                                type="button"
                                className="big-btn secondary"
                                onClick={() => navigate('/doctor/sharing')}
                            >
                                Powrót
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default DoctorPatientJournal;
