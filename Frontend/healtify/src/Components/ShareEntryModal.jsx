import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import '../css/doctor.css';
import { GetMyDoctors } from '../service/sharingService';
import { UpdateEntryShares } from '../service/dataService';

/**
 * Wybór lekarzy, którzy mają widzieć dany wpis z dziennika.
 */
function ShareEntryModal({ isOpen, onClose, onSaved, entry }) {
    const [doctors, setDoctors] = useState([]);
    const [selected, setSelected] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!isOpen || !entry) return;

        setLoading(true);
        setError('');
        // Stan bierzemy z wpisu - lista wpisów przychodzi razem z udostępnieniami.
        setSelected(entry.sharedWithDoctorIds ?? []);

        GetMyDoctors()
            .then((fetched) => setDoctors(fetched))
            .catch((err) => {
                console.log(err);
                setError('Nie udało się pobrać listy lekarzy.');
            })
            .finally(() => setLoading(false));
    }, [isOpen, entry]);

    if (!isOpen || !entry) {
        return null;
    }

    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget && !saving) {
            onClose();
        }
    };

    const toggleDoctor = (doctorId) => {
        setSelected((previous) =>
            previous.includes(doctorId)
                ? previous.filter((id) => id !== doctorId)
                : [...previous, doctorId]
        );
    };

    const handleSave = async () => {
        setSaving(true);
        setError('');

        try {
            const saved = await UpdateEntryShares(entry.entryId, selected);
            onSaved(saved);
        } catch (err) {
            console.log(err);
            setError(err.response?.data?.message || 'Nie udało się zapisać udostępnienia.');
            setSaving(false);
        }
    };

    const doctorLabel = (doctor) =>
        [doctor.title, doctor.doctorName].filter(Boolean).join(' ');

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Udostępnij wpis</h2>
                    <button
                        type="button"
                        className="modal-close"
                        onClick={onClose}
                        disabled={saving}
                        aria-label="Zamknij"
                    >
                        ×
                    </button>
                </div>

                <div className="modal-form">
                    <p className="field-label">„{entry.title}"</p>

                    {loading && <p>Wczytywanie...</p>}

                    {!loading && doctors.length === 0 && (
                        <p className="modal-warning">
                            Nie masz jeszcze żadnego lekarza. Znajdź go w zakładce Lekarze -
                            wpisy można udostępniać dopiero po nawiązaniu opieki.
                        </p>
                    )}

                    {!loading && doctors.map((doctor) => (
                        <label className="share-doctor-row" key={doctor.doctorId}>
                            <input
                                type="checkbox"
                                checked={selected.includes(doctor.doctorId)}
                                onChange={() => toggleDoctor(doctor.doctorId)}
                                disabled={saving}
                            />
                            <span>
                                <strong>{doctorLabel(doctor)}</strong>
                                <span className="doctor-list-sub">
                                    {doctor.specialization || 'Brak specjalizacji'}
                                </span>
                            </span>
                        </label>
                    ))}

                    {error && <div id="messages">{error}</div>}

                    <div className="modal-actions">
                        <button
                            type="button"
                            className="modal-btn secondary"
                            onClick={onClose}
                            disabled={saving}
                        >
                            Anuluj
                        </button>
                        <button
                            type="button"
                            className="modal-btn primary"
                            onClick={handleSave}
                            disabled={saving || loading || doctors.length === 0}
                        >
                            {saving ? 'Zapisywanie...' : 'Zapisz'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ShareEntryModal;
