import React, { useEffect, useState } from 'react';
import '../css/calendar.css';
import Lock from '../images/lock.svg';
import { deleteUser } from '../service/userService';

/**
 * Potwierdzenie usunięcia własnego konta.
 *
 * Zabezpieczenie: hasło - sprawdzane na backendzie - przed kimś,
 * kto trafił na otwartą sesję. Hasło żyje tylko w stanie tego komponentu i znika
 * razem z zamknięciem modala.
 */
function DeleteAccountModal({ isOpen, onClose, onDeleted, username }) {
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        if (isOpen) {
            setPassword('');
            setError('');
            setDeleting(false);
        }
    }, [isOpen]);

    if (!isOpen) {
        return null;
    }

    // W trakcie kasowania nie zamykamy modala - żądanie i tak jest już w drodze.
    const handleOverlayClick = (event) => {
        if (event.target === event.currentTarget && !deleting) {
            onClose();
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (password === '') {
            setError('Podaj hasło, żeby potwierdzić usunięcie konta.');
            return;
        }

        setDeleting(true);
        setError('');

        try {
            await deleteUser(password);
            onDeleted();
        } catch (err) {
            console.log(err);
            // 401 to zawsze złe hasło - konto zostaje nietknięte, można spróbować ponownie.
            setError(
                err.response?.status === 401
                    ? 'Nieprawidłowe hasło - konto nie zostało usunięte.'
                    : err.response?.data?.message || 'Nie udało się usunąć konta.'
            );
            setPassword('');
            setDeleting(false);
        }
    };

    return (
        <div className="modal-overlay" onMouseDown={handleOverlayClick}>
            <div className="modal-card" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <h2>Usunięcie konta</h2>
                    <button
                        type="button"
                        className="modal-close"
                        onClick={onClose}
                        disabled={deleting}
                        aria-label="Zamknij"
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <p className="modal-warning">
                        Zaraz usuniesz konto <strong>{username}</strong> razem ze wszystkimi
                        danymi: wpisami w dzienniku, umówionymi wizytami (terminy u lekarzy
                        się zwolnią), powiązaniami z lekarzami i danymi profilu.
                        <br />
                        <strong>Tej operacji nie da się cofnąć.</strong>
                    </p>

                    <label className="field-label" htmlFor="delete-password">Potwierdź hasłem</label>
                    <div className="modal-password">
                        <img src={Lock} alt="" />
                        <input
                            id="delete-password"
                            type="password"
                            name="password"
                            autoComplete="current-password"
                            autoFocus
                            placeholder="Wprowadź swoje hasło"
                            value={password}
                            onChange={(e) => { setPassword(e.target.value); setError(''); }}
                        />
                    </div>

                    {error && <div id="messages">{error}</div>}

                    <div className="modal-actions">
                        <button
                            type="button"
                            className="modal-btn secondary"
                            onClick={onClose}
                            disabled={deleting}
                        >
                            Anuluj
                        </button>
                        <button type="submit" className="modal-btn danger" disabled={deleting}>
                            {deleting ? 'Usuwanie...' : 'Usuń konto na stałe'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default DeleteAccountModal;
