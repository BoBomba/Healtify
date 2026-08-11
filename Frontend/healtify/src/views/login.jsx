import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';
import { useState } from 'react';
import {validateLoginData} from "../utils/validateAuthData";

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    // Komunikat zostawiony przez stronę, z której tu trafiliśmy - wylogowanie albo
    // usunięcie konta. Czytany raz i od razu kasowany by nie został na kolejne wejście na stronę.
    const [notice] = useState(() => {
        const message = sessionStorage.getItem('authNotice');
        sessionStorage.removeItem('authNotice');
        return message;
    });
    //console.log(email, password);

    localStorage.removeItem('token');

    const loginSubmit = (event) => {
        event.preventDefault();
        console.log("Wysyłanie danych do logowania...");
        validateLoginData(email, password);
    };

    const handleReset = () => {
        setEmail('');
        setPassword('');
    };

    return (
        <div className="login">
            <div id="powrot">
                <Link to="/" id="logreg">Powrót</Link>
            </div>
            <div className="main-container">
                <h1>Login</h1>
                {notice && <div id="notice">{notice}</div>}
                <form action="/login" onSubmit={loginSubmit} onReset={handleReset}>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" value={email} onChange={e => setEmail(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" value={password} onChange={e => setPassword(e.target.value)}/>
                    </div>
                    <div id="messages">
                        // TODO: komunikaty o błędach walidacji.
                    </div>
                    <button id="logreg" type="submit">Zaloguj się</button>
                    <button id="logreg" type="reset">reset</button>
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
