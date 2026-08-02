import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import { AddJournalEntry } from '../service/dataService';
import { formatDateKey } from '../utils/calendarUtils';

const SYMPTOM_OPTIONS = [
    'Lęk', 'Smutek', 'Bezsenność', 'Zmęczenie', 'Drażliwość',
    'Napięcie', 'Problemy z koncentracją', 'Płaczliwość',
    'Napady paniki', 'Izolacja społeczna'
];

const SCALE_FACES = ['😞', '🙁', '😐', '🙂', '😄'];

const MAX_TITLE_LENGTH = 120;
const MAX_DESCRIPTION_LENGTH = 5000;
const MAX_CUSTOM_SYMPTOM_LENGTH = 60;

// Wizyty u psychologa nie są zakładane przez pacjenta, więc modal obsługuje
// wyłącznie wpisy do dziennika (tabela journal_entries po stronie backendu).
function AddEntryModal({ isOpen, onClose, onSaved, defaultDate }) {
    const [title, setTitle] = useState('');
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [scale, setScale] = useState(null);
    const [symptoms, setSymptoms] = useState([]);
    const [customSymptom, setCustomSymptom] = useState('');
    const [description, setDescription] = useState('');
    const [reminder, setReminder] = useState(false);
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (isOpen) {
            const base = defaultDate || new Date();
            setTitle('');
            // formatDateKey liczy datę lokalnie - toISOString() potrafiłoby cofnąć dzień o jeden.
            setDate(formatDateKey(base));
            setTime(new Date().toTimeString().slice(0, 5));
            setScale(null);
            setSymptoms([]);
            setCustomSymptom('');
            setDescription('');
            setReminder(false);
            setError('');
        }
    }, [isOpen, defaultDate]);

    if (!isOpen) {
        return null;
    }

    const toggleSymptom = (symptom) => {
        setSymptoms((prev) =>
            prev.includes(symptom) ? prev.filter((s) => s !== symptom) : [...prev, symptom]
        );
    };

    const addCustomSymptom = () => {
        const value = customSymptom.trim();
        if (value !== '' && !symptoms.includes(value)) {
            setSymptoms((prev) => [...prev, value]);
        }
        setCustomSymptom('');
    };

    const handleCustomSymptomKeyDown = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            addCustomSymptom();
        }
    };

    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget) {
            onClose();
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (title.trim() === '') {
            setError('Podaj tytuł wpisu.');
            return;
        }
        if (scale === null) {
            setError('Wybierz samopoczucie w skali od 1 do 5.');
            return;
        }
        if (date === '' || time === '') {
            setError('Podaj datę i godzinę wpisu.');
            return;
        }

        setSaving(true);
        setError('');

        const payload = {
            title: title.trim(),
            description: description.trim(),
            entryAt: `${date}T${time}`,
            moodScale: scale,
            symptoms,
            reminder,
        };

        try {
            const saved = await AddJournalEntry(payload);
            onSaved(saved);
            onClose();
        } catch (err) {
            console.log(err);
            setError('Nie udało się zapisać wpisu. Spróbuj ponownie.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Nowy wpis w dzienniku</h2>
                    <button type="button" className="modal-close" onClick={onClose} aria-label="Zamknij">×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <label className="field-label" htmlFor="entry-title">Tytuł</label>
                    <input
                        id="entry-title"
                        type="text"
                        maxLength={MAX_TITLE_LENGTH}
                        placeholder="Np. Trudny dzień w pracy"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />

                    <div className="field-row">
                        <div>
                            <label className="field-label" htmlFor="entry-date">Data</label>
                            <input
                                id="entry-date"
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                        </div>
                        <div>
                            <label className="field-label" htmlFor="entry-time">Godzina</label>
                            <input
                                id="entry-time"
                                type="time"
                                value={time}
                                onChange={(e) => setTime(e.target.value)}
                            />
                        </div>
                    </div>

                    <label className="field-label">Samopoczucie (1-5)</label>
                    <div className="scale-picker">
                        {SCALE_FACES.map((face, index) => {
                            const value = index + 1;
                            return (
                                <button
                                    type="button"
                                    key={value}
                                    className={scale === value ? 'scale-btn active' : 'scale-btn'}
                                    onClick={() => setScale(value)}
                                    aria-label={`Ocena ${value}`}
                                >
                                    <span className="scale-face">{face}</span>
                                    <span className="scale-number">{value}</span>
                                </button>
                            );
                        })}
                    </div>

                    <label className="field-label">Objawy</label>
                    <div className="tag-list">
                        {SYMPTOM_OPTIONS.map((symptom) => (
                            <button
                                type="button"
                                key={symptom}
                                className={symptoms.includes(symptom) ? 'tag-chip active' : 'tag-chip'}
                                onClick={() => toggleSymptom(symptom)}
                            >
                                {symptom}
                            </button>
                        ))}
                        {symptoms.filter((s) => !SYMPTOM_OPTIONS.includes(s)).map((symptom) => (
                            <button
                                type="button"
                                key={symptom}
                                className="tag-chip active"
                                onClick={() => toggleSymptom(symptom)}
                            >
                                {symptom}
                            </button>
                        ))}
                    </div>
                    <div className="custom-tag-input">
                        <input
                            type="text"
                            maxLength={MAX_CUSTOM_SYMPTOM_LENGTH}
                            placeholder="Dodaj własny objaw..."
                            value={customSymptom}
                            onChange={(e) => setCustomSymptom(e.target.value)}
                            onKeyDown={handleCustomSymptomKeyDown}
                        />
                        <button type="button" onClick={addCustomSymptom}>Dodaj</button>
                    </div>

                    <label className="field-label" htmlFor="entry-description">Opis</label>
                    <textarea
                        id="entry-description"
                        rows="3"
                        maxLength={MAX_DESCRIPTION_LENGTH}
                        placeholder="Opisz swój dzień, myśli albo to, co Cię dziś poruszyło..."
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />

                    <label className="reminder-checkbox">
                        <input
                            type="checkbox"
                            checked={reminder}
                            onChange={(e) => setReminder(e.target.checked)}
                        />
                        Przypomnij mi o tym wpisie
                    </label>

                    {error && <div id="messages">{error}</div>}

                    <div className="modal-actions">
                        <button type="button" className="modal-btn secondary" onClick={onClose}>Anuluj</button>
                        <button type="submit" className="modal-btn primary" disabled={saving}>
                            {saving ? 'Zapisywanie...' : 'Zapisz'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default AddEntryModal;
