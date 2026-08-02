import React, { useEffect, useMemo, useState } from 'react';
import Nav from '../Components/Nav';
import AddEntryModal from '../Components/AddEntryModal';
import '../css/dashboard.css';
import '../css/calendar.css';
import { validateToken } from '../service/authService';
import { GetJournalEntries } from '../service/dataService';
import { MONTH_NAMES, WEEKDAY_NAMES, getMonthMatrix, formatDateKey, isSameDay } from '../utils/calendarUtils';

// Ile wpisów mieści się w kratce dnia - resztę pokazujemy jako "+N".
const MAX_CHIPS_PER_DAY = 3;

// Kolor prostokąta zależy od samopoczucia, żeby miesiąc dawał się czytać jednym spojrzeniem.
const moodClass = (moodScale) => {
    if (moodScale <= 2) return 'mood-low';
    if (moodScale === 3) return 'mood-mid';
    return 'mood-high';
};

function CalendarPage() {
    const today = useMemo(() => new Date(), []);
    const [visibleMonth, setVisibleMonth] = useState(new Date(today.getFullYear(), today.getMonth(), 1));
    const [entries, setEntries] = useState([]);
    const [selectedDate, setSelectedDate] = useState(today);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [loadError, setLoadError] = useState('');

    useEffect(() => {
        validateToken();
        GetJournalEntries()
            .then((data) => setEntries(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wpisów.');
            });
    }, []);

    const entriesByDay = useMemo(() => {
        const map = {};
        entries.forEach((entry) => {
            if (!entry.entryAt) return;
            const key = entry.entryAt.slice(0, 10);
            if (!map[key]) map[key] = [];
            map[key].push(entry);
        });
        Object.values(map).forEach((dayEntries) => dayEntries.sort((a, b) => a.entryAt.localeCompare(b.entryAt)));
        return map;
    }, [entries]);

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

    const handleEntrySaved = (savedEntry) => {
        setEntries((prev) => [...prev, savedEntry]);
        if (savedEntry.entryAt) {
            const saved = new Date(savedEntry.entryAt);
            setSelectedDate(saved);
            setVisibleMonth(new Date(saved.getFullYear(), saved.getMonth(), 1));
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
                            <button type="button" className="modal-btn primary" onClick={() => setIsModalOpen(true)}>
                                + Dodaj wpis
                            </button>
                        </div>
                    </div>

                    <div className="calendar-legend">
                        <span><span className="legend-dot mood-low" /> Samopoczucie 1-2</span>
                        <span><span className="legend-dot mood-mid" /> Samopoczucie 3</span>
                        <span><span className="legend-dot mood-high" /> Samopoczucie 4-5</span>
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
                                            {dayEntries.slice(0, MAX_CHIPS_PER_DAY).map((entry) => (
                                                <span
                                                    className={`day-entry-chip ${moodClass(entry.moodScale)}`}
                                                    key={entry.entryId}
                                                    title={entry.title}
                                                >
                                                    {entry.title}
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
                                onClick={() => setIsModalOpen(true)}
                            >
                                + Dodaj do tego dnia
                            </button>
                        </div>

                        {selectedDayEntries.length === 0 && <p>Brak wpisów tego dnia.</p>}

                        {selectedDayEntries.map((entry) => (
                            <div className="day-event-item" key={entry.entryId}>
                                <div className="day-event-title">
                                    <span className={`legend-dot ${moodClass(entry.moodScale)}`} />
                                    <strong>{entry.title}</strong>
                                    <span className="day-event-time">{entry.entryAt.slice(11, 16)}</span>
                                </div>
                                <p>Samopoczucie: {entry.moodScale}/5</p>
                                {entry.symptoms && entry.symptoms.length > 0 && (
                                    <div className="tag-list">
                                        {entry.symptoms.map((symptom, i) => (
                                            <span className="tag-chip active" key={i}>{symptom}</span>
                                        ))}
                                    </div>
                                )}
                                {entry.description && <p>{entry.description}</p>}
                            </div>
                        ))}
                    </div>
                </div>
            </main>

            <AddEntryModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSaved={handleEntrySaved}
                defaultDate={selectedDate || today}
            />
        </div>
    );
}

export default CalendarPage;
