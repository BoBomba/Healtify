import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';

function Login() {
    return (
        <div className="login">
            <div id="powrot">
                <Link to="/" id="logreg">Powrót</Link>
            </div>
            <div className="main-container">
                <h1>Login</h1>
                <form action="/login" method="POST">
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" />
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" />
                    </div>
                    <div id="messages">
                        {/* {messages &&
                            messages.map((message, index) => (
                                <span key={index}>{message}</span>
                            ))} */}
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
