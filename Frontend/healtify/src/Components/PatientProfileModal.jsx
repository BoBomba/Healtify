import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import '../css/profile.css';
import PatientDetails from './PatientDetails';
import { GetPatientProfile } from '../service/doctorService';

/**
 * Pełne szczegółowe dane pacjenta dla lekarza.
 *
 * Dane pobieramy dopiero przy otwarciu,
 * lekarz ma tylko wizytówkę, reszta schodzi z serwera kiedy potrzebuje.
 */
function PatientProfileModal({ isOpen, onClose, patient }) {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!isOpen || !patient) return;

        setLoading(true);
        setError('');
        setProfile(null);

        GetPatientProfile(patient.userId)
            .then((fetched) => setProfile(fetched))
            .catch((err) => {
                console.log(err);
                setError(
                    err.response?.status === 403
                        ? 'Ten pacjent nie udostępnia Ci już swoich danych.'
                        : 'Nie udało się pobrać danych pacjenta.'
                );
            })
            .finally(() => setLoading(false));
    }, [isOpen, patient]);

    if (!isOpen || !patient) {
        return null;
    }

    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget) {
            onClose();
        }
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>{patient.fullName || patient.username}</h2>
                    <button type="button" className="modal-close" onClick={onClose} aria-label="Zamknij">
                        ×
                    </button>
                </div>

                <div className="modal-form">
                    <p className="doctor-list-sub">{patient.email}</p>

                    {loading && <p>Wczytywanie...</p>}
                    {error && <div id="messages">{error}</div>}

                    {!loading && !error && (
                        <>
                            {profile && profile.completed === false && (
                                <p className="modal-warning">
                                    Pacjent nie uzupełnił jeszcze swoich szczegółowych danych.
                                </p>
                            )}
                            <PatientDetails profile={profile} />
                        </>
                    )}

                    <div className="modal-actions">
                        <button type="button" className="modal-btn secondary" onClick={onClose}>
                            Zamknij
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PatientProfileModal;
