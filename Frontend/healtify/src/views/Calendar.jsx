import React, { useEffect, useMemo, useState } from 'react';
import Nav from '../Components/Nav';
import AddEntryModal from '../Components/AddEntryModal';
import ShareEntryModal from '../Components/ShareEntryModal';
import '../css/dashboard.css';
import '../css/calendar.css';
import { validateToken } from '../service/authService';
import { DeleteJournalEntry, GetJournalEntries } from '../service/dataService';
import { GetMyAppointments } from '../service/sharingService';
import { MONTH_NAMES, WEEKDAY_NAMES, getMonthMatrix, formatDateKey, isSameDay, moodClass } from '../utils/calendarUtils';

// Ile wpisow miesci sie w kratce dnia - reszte pokazujemy jako "+N".
const MAX_CHIPS_PER_DAY = 3;

// TODO: dodac export do kalendarzy: .ics i jako sub do Google Calendar, Outlook itp.

function CalendarPage() {
    const today = useMemo(() => new Date(), []);
    const [visibleMonth, setVisibleMonth] = useState(new Date(today.getFullYear(), today.getMonth(), 1));
    const [entries, setEntries] = useState([]);
    const [appointments, setAppointments] = useState([]);
    const [selectedDate, setSelectedDate] = useState(today);
    const [isModalOpen, setIsModalOpen] = useState(false);
    // Wpis otwarty do edycji. null = modal działa w trybie dodawania.
    const [editingEntry, setEditingEntry] = useState(null);
    const [sharingEntry, setSharingEntry] = useState(null);
    const [loadError, setLoadError] = useState('');

    const isShared = (entry) => (entry.sharedWithDoctorIds?.length ?? 0) > 0;

    /** Po zapisie udostępnień podmieniamy wpis w miejscu - data się nie zmienia. */
    const handleSharesSaved = (savedEntry) => {
        setEntries((prev) =>
            prev.map((item) => (item.entryId === savedEntry.entryId ? savedEntry : item))
        );
        setSharingEntry(null);
    };

    useEffect(() => {
        validateToken();
        GetJournalEntries()
            .then((data) => setEntries(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wpisów.');
            });
        // Wizyty zaklada lekarz - pacjent tylko do odczytu.
        GetMyAppointments()
            .then((data) => setAppointments(data))
            .catch((error) => console.log(error));
    }, []);

    // Wpisy i wizyty laduja razem w kalendarzu, bo kratka pokazuje i to i to.
    // Pole kind decyduje o kolorze prostokata i o tym, co się w nim wyswietli.
    const entriesByDay = useMemo(() => {
        const map = {};
        const push = (at, item) => {
            if (!at) return;
            const key = at.slice(0, 10);
            if (!map[key]) map[key] = [];
            map[key].push({ ...item, at });
        };

        entries.forEach((entry) => push(entry.entryAt, {
            kind: 'entry',
            key: `entry-${entry.entryId}`,
            entry,
        }));
        appointments.forEach((appointment) => push(appointment.appointmentAt, {
            kind: 'appointment',
            key: `appointment-${appointment.appointmentId}`,
            appointment,
        }));

        Object.values(map).forEach((dayItems) => dayItems.sort((a, b) => a.at.localeCompare(b.at)));
        return map;
    }, [entries, appointments]);

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

    // przy edycji podmieniamy wpis w miejscu, żeby nie zdublował się w kalendarzu.
    const handleEntrySaved = (savedEntry) => {
        setEntries((prev) => (
            prev.some((item) => item.entryId === savedEntry.entryId)
                ? prev.map((item) => (item.entryId === savedEntry.entryId ? savedEntry : item))
                : [...prev, savedEntry]
        ));
        if (savedEntry.entryAt) {
            const saved = new Date(savedEntry.entryAt);
            setSelectedDate(saved);
            setVisibleMonth(new Date(saved.getFullYear(), saved.getMonth(), 1));
        }
    };

    const openAddModal = () => {
        setEditingEntry(null);
        setIsModalOpen(true);
    };

    const openEditModal = (entry) => {
        setEditingEntry(entry);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingEntry(null);
    };

    const handleDeleteEntry = async (entry) => {
        const confirmed = window.confirm(
            `Usunąć wpis "${entry.title}"?\n\nTej operacji nie da się cofnąć.`
        );
        if (!confirmed) return;

        try {
            await DeleteJournalEntry(entry.entryId);
            setEntries((prev) => prev.filter((item) => item.entryId !== entry.entryId));
        } catch (error) {
            console.log(error);
            setLoadError('Nie udało się usunąć wpisu.');
        }
    };

    const selectedDayEntries = selectedDate ? (entriesByDay[formatDateKey(selectedDate)] || []) : [];

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
                                + Dodaj wpis
                            </button>
                        </div>
                    </div>

                    <div className="calendar-legend">
                        <span><span className="legend-dot mood-low" /> Samopoczucie 1-2</span>
                        <span><span className="legend-dot mood-mid" /> Samopoczucie 3</span>
                        <span><span className="legend-dot mood-high" /> Samopoczucie 4-5</span>
                        <span><span className="legend-dot appointment" /> Wizyta u lekarza</span>
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
                                const dayEntries = entriesByDay[formatDateKey(day)] || [];
                                const hiddenCount = dayEntries.length - MAX_CHIPS_PER_DAY;
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
                                            {dayEntries.slice(0, MAX_CHIPS_PER_DAY).map((item) => (
                                                <span
                                                    className={`day-entry-chip ${item.kind === 'appointment'
                                                        ? 'appointment'
                                                        : moodClass(item.entry.moodScale)}`}
                                                    key={item.key}
                                                    title={item.kind === 'appointment'
                                                        ? `Wizyta: ${item.appointment.title}`
                                                        : item.entry.title}
                                                >
                                                    {item.kind === 'appointment'
                                                        ? item.appointment.title
                                                        : item.entry.title}
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
                                + Dodaj do tego dnia
                            </button>
                        </div>

                        {selectedDayEntries.length === 0 && <p>Brak wpisów tego dnia.</p>}

                        {selectedDayEntries.map((item) => (
                            item.kind === 'appointment' ? (
                                <div className="day-event-item" key={item.key}>
                                    <div className="day-event-title">
                                        <span className="legend-dot appointment" />
                                        <strong>{item.appointment.title}</strong>
                                        <span className="day-event-time">{item.at.slice(11, 16)}</span>
                                    </div>
                                    <p>Wizyta u: {item.appointment.doctor.doctorName}</p>
                                    {item.appointment.notes && <p>{item.appointment.notes}</p>}
                                </div>
                            ) : (
                                <div className="day-event-item" key={item.key}>
                                    <div className="day-event-title">
                                        <span className={`legend-dot ${moodClass(item.entry.moodScale)}`} />
                                        <strong>{item.entry.title}</strong>
                                        <span className="day-event-time">{item.at.slice(11, 16)}</span>
                                        {/* Wizyt pacjent nie rusza - zakłada je lekarz, więc przyciski
                                            są tylko przy własnych wpisach. */}
                                        <div className="day-event-actions">
                                            <button
                                                type="button"
                                                className={`modal-btn share small${isShared(item.entry) ? ' active' : ''}`}
                                                onClick={() => setSharingEntry(item.entry)}
                                            >
                                                {isShared(item.entry) ? 'Udostępniony' : 'Udostępnij'}
                                            </button>
                                            <button
                                                type="button"
                                                className="modal-btn secondary small"
                                                onClick={() => openEditModal(item.entry)}
                                            >
                                                Edytuj
                                            </button>
                                            <button
                                                type="button"
                                                className="modal-btn danger small"
                                                onClick={() => handleDeleteEntry(item.entry)}
                                            >
                                                Usuń
                                            </button>
                                        </div>
                                    </div>
                                    <p>Samopoczucie: {item.entry.moodScale}/5</p>
                                    {item.entry.symptoms && item.entry.symptoms.length > 0 && (
                                        <div className="tag-list">
                                            {item.entry.symptoms.map((symptom, i) => (
                                                <span className={`tag-chip ${moodClass(item.entry.moodScale)}`} key={i}>
                                                    {symptom}
                                                </span>
                                            ))}
                                        </div>
                                    )}
                                    {item.entry.description && <p>{item.entry.description}</p>}
                                </div>
                            )
                        ))}
                    </div>
                </div>
            </main>

            <AddEntryModal
                isOpen={isModalOpen}
                onClose={closeModal}
                onSaved={handleEntrySaved}
                defaultDate={selectedDate || today}
                entry={editingEntry}
            />

            <ShareEntryModal
                isOpen={sharingEntry !== null}
                onClose={() => setSharingEntry(null)}
                onSaved={handleSharesSaved}
                entry={sharingEntry}
            />
        </div>
    );
}

export default CalendarPage;
