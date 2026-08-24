import React, { useState } from 'react';
import '../css/calendar.css';
import { buildAppointmentsIcs, downloadIcs, filterUpcoming } from '../utils/icsUtils';
import { formatDateKey } from '../utils/calendarUtils';

/**
 * Wybor zakresu i pobranie wizyt jako .ics.
 * role: 'patient' albo 'doctor' - zmienia tylko opisy w wygenerowanych wydarzeniach.
 */
function ExportCalendarModal({ isOpen, onClose, appointments, role }) {
    const [scope, setScope] = useState('upcoming');

    if (!isOpen) {
        return null;
    }

    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget) {
            onClose();
        }
    };

    const upcoming = filterUpcoming(appointments);
    const selected = scope === 'all' ? appointments : upcoming;

    const handleExport = () => {
        downloadIcs(
            buildAppointmentsIcs(selected, role),
            `healtify-wizyty-${formatDateKey(new Date())}.ics`
        );
        onClose();
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Eksport wizyt</h2>
                    <button type="button" className="modal-close" onClick={onClose} aria-label="Zamknij">
                        ×
                    </button>
                </div>

                <div className="modal-form">
                    <p className="field-label">Które wizyty zapisać do pliku?</p>

                    <label className="export-scope-row">
                        <input
                            type="radio"
                            name="export-scope"
                            checked={scope === 'upcoming'}
                            onChange={() => setScope('upcoming')}
                        />
                        <span>
                            <strong>Nadchodzące</strong>
                            <span className="doctor-list-sub">Od dziś w górę - {upcoming.length}</span>
                        </span>
                    </label>

                    <label className="export-scope-row">
                        <input
                            type="radio"
                            name="export-scope"
                            checked={scope === 'all'}
                            onChange={() => setScope('all')}
                        />
                        <span>
                            <strong>Wszystkie</strong>
                            <span className="doctor-list-sub">Razem z historią - {appointments.length}</span>
                        </span>
                    </label>

                    <p className="modal-warning">
                        Plik .ics zaimportujesz do Kalendarza Google, Outlooka czy Apple Calendar.
                        Każda wizyta trwa w nim godzinę - to eksport jednorazowy, późniejsze zmiany
                        w Healtify nie zaktualizują się same.
                    </p>

                    <div className="modal-actions">
                        <button type="button" className="modal-btn secondary" onClick={onClose}>
                            Anuluj
                        </button>
                        <button
                            type="button"
                            className="modal-btn primary"
                            onClick={handleExport}
                            disabled={selected.length === 0}
                        >
                            Pobierz .ics
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ExportCalendarModal;
