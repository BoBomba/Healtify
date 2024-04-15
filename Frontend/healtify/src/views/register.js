import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';

function Register() {
    return (
        <div className="register">
            <div id="powrot">
                <Link to="/" id="logreg">Powrót</Link>
            </div>
            <div className="main-container" style={{ gap: 0 }}>
                <h1>Rejestracja</h1>
                <form action="/register" method="POST">
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="text" id="firstname" name="name" placeholder="Wprowadź Imie" />
                    </div>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="text" id="lastname" name="surname" placeholder="Wprowadź Nazwisko" />
                    </div>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" />
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" />
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="repeated_password" placeholder="Wprowadź ponownie hasło" />
                    </div>
                    <div id="messages">
                        {/* {messages &&
                            messages.map((message, index) => (
                                <span key={index}>{message}</span>
                            ))} */}
                    </div>
                    <button id="logreg" type="submit">Zarejestruj się</button>
                    <button id="logreg" type="reset">reset</button>
                </form>
            </div>
        </div>
    );
}

export default Register;
