import React, { useEffect, useMemo, useState } from 'react';
import Nav from '../../Components/Nav';
import AddAppointmentModal from '../../Components/AddAppointmentModal';
import '../../css/dashboard.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { DeleteAppointment, GetAppointments, GetMyPatients } from '../../service/doctorService';
import { formatAppointmentTime, groupAppointmentsByDay } from '../../utils/appointmentUtils';
import { MONTH_NAMES, WEEKDAY_NAMES, getMonthMatrix, formatDateKey, isSameDay } from '../../utils/calendarUtils';

// Ile wizyt mieści się w kratce dnia - resztę pokazujemy jako "+N".
const MAX_CHIPS_PER_DAY = 3;

/**
 * Kalendarz lekarza. nie ma tu wpisów - wyłącznie wizyty, na niebiesko.
 */
function DoctorCalendar() {
    const { doctor } = useDoctorGuard();
    const today = useMemo(() => new Date(), []);
    const [visibleMonth, setVisibleMonth] = useState(new Date(today.getFullYear(), today.getMonth(), 1));
    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    const [selectedDate, setSelectedDate] = useState(today);
    const [isModalOpen, setIsModalOpen] = useState(false);
    // Wizyta otwarta do edycji. null = modal działa w trybie zakładania nowej.
    const [editingAppointment, setEditingAppointment] = useState(null);
    const [loadError, setLoadError] = useState('');

    useEffect(() => {
        if (!doctor) return;

        // Kalendarz pozwala cofnąć się do poprzednich miesięcy, więc bierze też wizyty z przeszłości.
        GetAppointments('all')
            .then((data) => setAppointments(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wizyt.');
            });
        GetMyPatients()
            .then((data) => setPatients(data))
            .catch((error) => console.log(error));
    }, [doctor]);

    const appointmentsByDay = useMemo(() => groupAppointmentsByDay(appointments), [appointments]);

    const weeks = useMemo(
        () => getMonthMatrix(visibleMonth.getFullYear(), visibleMonth.getMonth()),
        [visibleMonth]
    );

    const goToPrevMonth = () => {
        setVisibleMonth((prev) => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
    };

    const goToNextMonth = () => {
        setVisibleMonth((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
    };

    const goToToday = () => {
        setVisibleMonth(new Date(today.getFullYear(), today.getMonth(), 1));
        setSelectedDate(today);
    };

    // Ten sam handler obsługuje założenie i edycję - przy edycji podmieniamy wizytę
    // w miejscu, żeby nie zdublowała się w kalendarzu.
    const handleAppointmentSaved = (saved) => {
        setAppointments((prev) => (
            prev.some((item) => item.appointmentId === saved.appointmentId)
                ? prev.map((item) => (item.appointmentId === saved.appointmentId ? saved : item))
                : [...prev, saved]
        ));
        if (saved.appointmentAt) {
            const savedDate = new Date(saved.appointmentAt);
            setSelectedDate(savedDate);
            setVisibleMonth(new Date(savedDate.getFullYear(), savedDate.getMonth(), 1));
        }
    };

    const openAddModal = () => {
        setEditingAppointment(null);
        setIsModalOpen(true);
    };

    const openEditModal = (appointment) => {
        setEditingAppointment(appointment);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingAppointment(null);
    };

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

    const selectedDayAppointments = selectedDate
        ? (appointmentsByDay[formatDateKey(selectedDate)] || [])
        : [];

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="calendar-page">
                    <div className="calendar-toolbar">
                        <div className="calendar-nav-buttons">
                            <button type="button" onClick={goToPrevMonth} aria-label="Poprzedni miesiąc">‹</button>
                            <h2>{MONTH_NAMES[visibleMonth.getMonth()]} {visibleMonth.getFullYear()}</h2>
                            <button type="button" onClick={goToNextMonth} aria-label="Następny miesiąc">›</button>
                        </div>
                        <div className="calendar-toolbar-actions">
                            <button type="button" className="modal-btn secondary" onClick={goToToday}>Dziś</button>
                            <button type="button" className="modal-btn primary" onClick={openAddModal}>
                                + Umów wizytę
                            </button>
                        </div>
                    </div>

                    <div className="calendar-legend">
                        <span><span className="legend-dot appointment" /> Wizyta z pacjentem</span>
                    </div>

                    {loadError && <div id="messages">{loadError}</div>}

                    <div className="calendar-grid">
                        {WEEKDAY_NAMES.map((day) => (
                            <div className="calendar-weekday" key={day}>{day}</div>
                        ))}
                        {weeks.map((week, weekIndex) => (
                            week.map((day, dayIndex) => {
                                if (!day) {
                                    return <div className="calendar-cell empty" key={`${weekIndex}-${dayIndex}`} />;
                                }
                                const dayAppointments = appointmentsByDay[formatDateKey(day)] || [];
                                const hiddenCount = dayAppointments.length - MAX_CHIPS_PER_DAY;
                                const cellClasses = [
                                    'calendar-cell',
                                    isSameDay(day, today) ? 'is-today' : '',
                                    isSameDay(day, selectedDate) ? 'is-selected' : ''
                                ].join(' ').trim();

                                return (
                                    <button
                                        type="button"
                                        className={cellClasses}
                                        key={formatDateKey(day)}
                                        onClick={() => setSelectedDate(day)}
                                    >
                                        <span className="calendar-day-number">{day.getDate()}</span>
                                        <span className="calendar-day-entries">
                                            {dayAppointments.slice(0, MAX_CHIPS_PER_DAY).map((appointment) => (
                                                <span
                                                    className="day-entry-chip appointment"
                                                    key={appointment.appointmentId}
                                                    title={`${formatAppointmentTime(appointment.appointmentAt)} ${appointment.title} - ${appointment.patient.username}`}
                                                >
                                                    {formatAppointmentTime(appointment.appointmentAt)} {appointment.patient.username}
                                                </span>
                                            ))}
                                            {hiddenCount > 0 && (
                                                <span className="day-entry-more">+{hiddenCount}</span>
                                            )}
                                        </span>
                                    </button>
                                );
                            })
                        ))}
                    </div>

                    <div className="datablock calendar-day-panel">
                        <div className="calendar-day-panel-header">
                            <h3>{selectedDate ? formatDateKey(selectedDate) : 'Wybierz dzień'}</h3>
                            <button
                                type="button"
                                className="modal-btn primary small"
                                onClick={openAddModal}
                            >
                                + Umów na ten dzień
                            </button>
                        </div>

                        {selectedDayAppointments.length === 0 && <p>Brak wizyt tego dnia.</p>}

                        {selectedDayAppointments.map((appointment) => (
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
                                            onClick={() => openEditModal(appointment)}
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
                </div>
            </main>

            <AddAppointmentModal
                isOpen={isModalOpen}
                onClose={closeModal}
                onSaved={handleAppointmentSaved}
                defaultDate={selectedDate || today}
                patients={patients}
                appointment={editingAppointment}
            />
        </div>
    );
}

export default DoctorCalendar;
