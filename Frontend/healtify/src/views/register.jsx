import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';
import { useState } from 'react';
import {validateRegisterData} from "../utils/validateAuthData";

function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    console.log(username, email, password, confirmPassword);

    const registerSubmit = (event) => {
        event.preventDefault();
        console.log("Wysyłanie danych do validatora rejestracji...");
        validateRegisterData(username, email, password, confirmPassword);
    };

    return (
        <div className="register">
            <div id="powrot">
                <Link to="/" id="logreg">Powrót</Link>
            </div>
            <div className="main-container" style={{ gap: 0 }}>
                <h1>Rejestracja</h1>

                <form method="POST" onSubmit={registerSubmit}>

                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="text" id="firstname" name="name" placeholder="Wprowadź Nazwe Uzytkownika" value={username} onChange={e => setUsername(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" value={email} onChange={e => setEmail(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" value={password} onChange={e => setPassword(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="repeated_password" placeholder="Wprowadź ponownie hasło" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)}/>
                    </div>
                    <button id="logreg" type="submit">Zarejestruj się</button>
                    <button id="logreg" type="reset">reset</button>
                </form>
            </div>
        </div>
    );
}

export default Register;
