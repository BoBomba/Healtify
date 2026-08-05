import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';

function ForgotPassword() {
    return (
        <div className="forgot-password">
            <div id="powrot">
                <Link to="/login" id="logreg">Powrót</Link>
            </div>
            <div className="main-container">
                <h1>Reset hasła</h1>
                <p style={{ textAlign: 'center', padding: '0 1em' }}>
                    Ta opcja nie jest jeszcze dostępna.<br />
                    Pracujemy nad możliwością resetowania hasła.
                </p>
                <Link id="logreg" to="/login">Wróć do logowania</Link>
            </div>
        </div>
    );
}

export default ForgotPassword;
