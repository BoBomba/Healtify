import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import { AddAppointment } from '../service/doctorService';
import { formatDateKey } from '../utils/calendarUtils';

const MAX_TITLE_LENGTH = 120;
const MAX_NOTES_LENGTH = 5000;

// Wizytę zakłada wyłącznie lekarz i tylko pacjentowi z listy przypisanych -
// backend i tak sprawdza powiązanie, ale nie ma sensu pokazywać kogoś, komu i tak się nie da.
function AddAppointmentModal({ isOpen, onClose, onSaved, defaultDate, patients }) {
    const [patientId, setPatientId] = useState('');
    const [title, setTitle] = useState('');
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [notes, setNotes] = useState('');
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (isOpen) {
            const base = defaultDate || new Date();
            setPatientId(patients.length === 1 ? String(patients[0].userId) : '');
            setTitle('');
            // formatDateKey liczy datę lokalnie - toISOString() potrafiłoby cofnąć dzień o jeden.
            setDate(formatDateKey(base));
            setTime('12:00');
            setNotes('');
            setError('');
        }
    }, [isOpen, defaultDate, patients]);

    if (!isOpen) {
        return null;
    }

    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget) {
            onClose();
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (patientId === '') {
            setError('Wybierz pacjenta.');
            return;
        }
        if (title.trim() === '') {
            setError('Podaj tytuł wizyty.');
            return;
        }
        if (date === '' || time === '') {
            setError('Podaj datę i godzinę wizyty.');
            return;
        }

        setSaving(true);
        setError('');

        const payload = {
            patientId: Number(patientId),
            title: title.trim(),
            notes: notes.trim(),
            appointmentAt: `${date}T${time}`,
        };

        try {
            const saved = await AddAppointment(payload);
            onSaved(saved);
            onClose();
        } catch (err) {
            console.log(err);
            setError('Nie udało się zapisać wizyty. Spróbuj ponownie.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Nowa wizyta</h2>
                    <button type="button" className="modal-close" onClick={onClose} aria-label="Zamknij">×</button>
                </div>

                {patients.length === 0 ? (
                    <p>
                        Nie masz jeszcze przypisanych pacjentów. Zaproś kogoś w zakładce
                        Udostepnianie - wizytę można umówić dopiero po akceptacji.
                    </p>
                ) : (
                    <form onSubmit={handleSubmit} className="modal-form">
                        <label className="field-label" htmlFor="appointment-patient">Pacjent</label>
                        <select
                            id="appointment-patient"
                            value={patientId}
                            onChange={(e) => setPatientId(e.target.value)}
                        >
                            <option value="">Wybierz pacjenta...</option>
                            {patients.map((patient) => (
                                <option key={patient.userId} value={patient.userId}>
                                    {patient.username}
                                </option>
                            ))}
                        </select>

                        <label className="field-label" htmlFor="appointment-title">Tytuł</label>
                        <input
                            id="appointment-title"
                            type="text"
                            maxLength={MAX_TITLE_LENGTH}
                            placeholder="Np. Konsultacja kontrolna"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />

                        <div className="field-row">
                            <div>
                                <label className="field-label" htmlFor="appointment-date">Data</label>
                                <input
                                    id="appointment-date"
                                    type="date"
                                    value={date}
                                    onChange={(e) => setDate(e.target.value)}
                                />
                            </div>
                            <div>
                                <label className="field-label" htmlFor="appointment-time">Godzina</label>
                                <input
                                    id="appointment-time"
                                    type="time"
                                    value={time}
                                    onChange={(e) => setTime(e.target.value)}
                                />
                            </div>
                        </div>

                        <label className="field-label" htmlFor="appointment-notes">Notatka</label>
                        <textarea
                            id="appointment-notes"
                            rows="3"
                            maxLength={MAX_NOTES_LENGTH}
                            placeholder="Temat spotkania, ustalenia, o czym pamiętać..."
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                        />

                        {error && <div id="messages">{error}</div>}

                        <div className="modal-actions">
                            <button type="button" className="modal-btn secondary" onClick={onClose}>Anuluj</button>
                            <button type="submit" className="modal-btn primary" disabled={saving}>
                                {saving ? 'Zapisywanie...' : 'Umów wizytę'}
                            </button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
}

export default AddAppointmentModal;
