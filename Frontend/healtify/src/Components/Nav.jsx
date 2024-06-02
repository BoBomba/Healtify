import React from 'react'
import { Link } from 'react-router-dom'
import '../css/dashboard.css'
import SettingsIcon from '../images/settings_icon.svg'
import TextLogo from '../images/Healtify_white.svg'
import { toggleMenu, useOutsideClick } from './Navbar';

function Nav() {
    useOutsideClick();
    const username = localStorage.getItem('username');
  return (
    <div>
        <nav>
                <a id="navMenu" onClick={toggleMenu}>
                    <img id="settings" src={SettingsIcon} alt="Settings" onClick={toggleMenu} />
                </a>
                {/* TODO zrobic wyswietlanie zdjec */}
                <img id="textlogo" src={TextLogo} alt="Logo" />
                <img id="logo" src="" alt="Logo" />
        </nav>

        <div className="navbar" id="myNavbar">
                <p>
                {username}
                </p>
                <Link to="/dashboard">Dashboard</Link>
                <Link to="/data">Przeglądaj Dane</Link>
                <Link to="/sharing">Udostepnianie</Link>
                <Link to="/Settings">Ustawienia</Link>
                <Link to="/logout">Wyloguj</Link>
            </div>
    </div>
    )
}

export default Nav