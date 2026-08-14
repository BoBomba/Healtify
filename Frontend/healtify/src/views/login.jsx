import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';
import { useEffect, useState } from 'react';
import {validateLoginData} from "../utils/validateAuthData";

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [validationErrors, setValidationErrors] = useState([]);
    const [submitting, setSubmitting] = useState(false);
    // Komunikat zostawiony przez stronę, z której trafiliśmy - wylogowanie albo
    // usunięcie konta. Czytany raz i od razu kasowany.
    const [notice] = useState(() => {
        const message = sessionStorage.getItem('authNotice');
        sessionStorage.removeItem('authNotice');
        return message;
    });

    // Kasuje token przy montowaniu komponentu
    useEffect(() => {
        localStorage.removeItem('token');
    }, []);

    const loginSubmit = async (event) => {
        event.preventDefault();
        const errors = [];

        if (!email.trim() || !password) {
            errors.push('Wypełnij wszystkie pola.');
        } else if (!email.includes('@')) {
            errors.push('Wprowadź poprawny adres e-mail.');
        }

        setValidationErrors(errors);
        if (errors.length > 0) return;

        console.log("Wysyłanie danych do logowania...");
        setSubmitting(true);

        // Błąd zwraca tutaj jako tekst.
        // przy sukcesie przeglądarka przechodzi na panel użytkownika.
        const failure = await validateLoginData(email, password);

        // Przy sukcesie przycisk zablokowany - przekierowanie.
        if (failure) {
            setSubmitting(false);
            setValidationErrors([failure]);
        }
    };

    const handleReset = () => {
        setEmail('');
        setPassword('');
        setValidationErrors([]);
    };

    return (
        <div className="login">
            <div id="powrot">
                <Link to="/" id="logreg">Powrót</Link>
            </div>
            <div className="main-container">
                <h1>Login</h1>
                {notice && <div id="notice">{notice}</div>}
                <form action="/login" onSubmit={loginSubmit} onReset={handleReset} noValidate>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" value={email} onChange={e => setEmail(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" value={password} onChange={e => setPassword(e.target.value)}/>
                    </div>
                    <div id="messages">
                        {validationErrors.map(error => (
                            <div key={error} style={{ whiteSpace: 'pre-line' }}>{error}</div>
                        ))}
                    </div>
                    <button id="logreg" type="submit" disabled={submitting}>
                        {submitting ? 'Logowanie...' : 'Zaloguj się'}
                    </button>
                    <button id="logreg" type="reset" disabled={submitting}>reset</button>
                </form>
                <Link id="forgot" to="/forgotpasswd">Zapomniałeś hasła?</Link>
            </div>
            <div id="regbutton">
                <Link id="logreg" to="/register">Zarejestruj się</Link>
            </div>
        </div>
    );
}

export default Login;
