import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import { grantDoctorRole } from '../service/adminService';

/**
 * Potwierdzenie nadania roli lekarza.
 * profil zakłada się pusty, a lekarz uzupełnia go sam przy pierwszym zalogowaniu. 
 */
function GrantDoctorModal({ isOpen, onClose, onGranted, user }) {
    const [error, setError] = useState('');
    const [granting, setGranting] = useState(false);

    useEffect(() => {
        if (isOpen) {
            setError('');
            setGranting(false);
        }
    }, [isOpen]);

    if (!isOpen || !user) {
        return null;
    }

    // W trakcie nadawania nie zamykamy modala - żądanie jest już w drodze.
    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget && !granting) {
            onClose();
        }
    };

    const handleConfirm = async () => {
        setGranting(true);
        setError('');

        try {
            await grantDoctorRole(user.userId);
            onGranted(user);
        } catch (err) {
            console.log(err);
            setError(err.response?.data?.message || 'Nie udało się nadać roli lekarza.');
            setGranting(false);
        }
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Nadanie roli lekarza</h2>
                    <button
                        type="button"
                        className="modal-close"
                        onClick={onClose}
                        disabled={granting}
                        aria-label="Zamknij"
                    >
                        ×
                    </button>
                </div>

                <div className="modal-form">
                    <p>
                        Czy na pewno nadać rolę lekarza kontu <strong>{user.username}</strong>?
                    </p>
                    <p className="modal-warning">
                        Konto straci panel pacjenta, a zyska panel lekarza. Swoje dane zawodowe
                        (imię i nazwisko, specjalizację, numer PWZ) lekarz uzupełni sam przy
                        pierwszym zalogowaniu.
                    </p>

                    {error && <div id="messages">{error}</div>}

                    <div className="modal-actions">
                        <button
                            type="button"
                            className="modal-btn secondary"
                            onClick={onClose}
                            disabled={granting}
                        >
                            Anuluj
                        </button>
                        <button
                            type="button"
                            className="modal-btn primary"
                            onClick={handleConfirm}
                            disabled={granting}
                        >
                            {granting ? 'Nadawanie...' : 'Nadaj rolę'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default GrantDoctorModal;
