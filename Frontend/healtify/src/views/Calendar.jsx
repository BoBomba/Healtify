import React, { useEffect, useMemo, useState } from 'react';
import Nav from '../Components/Nav';
import AddEventModal from '../Components/AddEventModal';
import '../css/dashboard.css';
import '../css/calendar.css';
import { validateToken } from '../service/authService';
import { GetCalendarEvents } from '../service/dataService';
import { MONTH_NAMES, WEEKDAY_NAMES, getMonthMatrix, formatDateKey, isSameDay } from '../utils/calendarUtils';

const normalizeEvents = (data) => {
    if (!data || data === 'Brak danych' || data === 'null') {
        return [];
    }
    return Array.isArray(data) ? data : [data];
};

function CalendarPage() {
    const today = useMemo(() => new Date(), []);
    const [visibleMonth, setVisibleMonth] = useState(new Date(today.getFullYear(), today.getMonth(), 1));
    const [events, setEvents] = useState([]);
    const [selectedDate, setSelectedDate] = useState(today);
    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        validateToken();
        GetCalendarEvents().then((data) => setEvents(normalizeEvents(data)));
    }, []);

    const eventsByDay = useMemo(() => {
        const map = {};
        events.forEach((event) => {
            if (!event.eventStart) return;
            const key = event.eventStart.slice(0, 10);
            if (!map[key]) map[key] = [];
            map[key].push(event);
        });
        return map;
    }, [events]);

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

    const handleEventSaved = (savedEvent) => {
        setEvents((prev) => [...prev, savedEvent]);
    };

    const selectedDayEvents = selectedDate ? (eventsByDay[formatDateKey(selectedDate)] || []) : [];

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
                        <span><span className="legend-dot entry-dot" /> Wpis dziennika</span>
                        <span><span className="legend-dot visit-dot" /> Wizyta u psychologa</span>
                    </div>

                    <div className="calendar-grid">
                        {WEEKDAY_NAMES.map((day) => (
                            <div className="calendar-weekday" key={day}>{day}</div>
                        ))}
                        {weeks.map((week, weekIndex) => (
                            week.map((day, dayIndex) => {
                                if (!day) {
                                    return <div className="calendar-cell empty" key={`${weekIndex}-${dayIndex}`} />;
                                }
                                const dayEvents = eventsByDay[formatDateKey(day)] || [];
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
                                        <div className="calendar-day-dots">
                                            {dayEvents.slice(0, 3).map((event, index) => (
                                                <span
                                                    key={index}
                                                    className={`legend-dot ${event.eventType === 'THERAPY_VISIT' ? 'visit-dot' : 'entry-dot'}`}
                                                />
                                            ))}
                                        </div>
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

                        {selectedDayEvents.length === 0 && <p>Brak zdarzeń tego dnia.</p>}

                        {selectedDayEvents.map((event, index) => (
                            <div className="day-event-item" key={index}>
                                <div className="day-event-title">
                                    <span className={`legend-dot ${event.eventType === 'THERAPY_VISIT' ? 'visit-dot' : 'entry-dot'}`} />
                                    <strong>{event.eventTitle}</strong>
                                    {event.eventStart && <span className="day-event-time">{event.eventStart.slice(11, 16)}</span>}
                                </div>
                                {event.moodScale && <p>Samopoczucie: {event.moodScale}/5</p>}
                                {event.symptoms && event.symptoms.length > 0 && (
                                    <div className="tag-list">
                                        {event.symptoms.map((symptom, i) => (
                                            <span className="tag-chip active" key={i}>{symptom}</span>
                                        ))}
                                    </div>
                                )}
                                {event.eventDescription && <p>{event.eventDescription}</p>}
                            </div>
                        ))}
                    </div>
                </div>
            </main>

            <AddEventModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSaved={handleEventSaved}
                defaultDate={selectedDate || today}
            />
        </div>
    );
}

export default CalendarPage;
