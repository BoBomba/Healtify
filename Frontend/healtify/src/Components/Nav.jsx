
import React, { useEffect , useState} from 'react'
import { Link } from 'react-router-dom'
import '../css/dashboard.css'
import SettingsIcon from '../images/settings_icon.svg'
import TextLogo from '../images/Healtify_white.svg'
import { toggleMenu, useOutsideClick } from './Navbar';
import { checkAdminStatus } from '../service/adminService'
import logo from '../images/logo.png'

function Nav() {
    useOutsideClick();
    const [isAdmin, setIsAdmin] = useState(null);
    const username = localStorage.getItem('username');
    useEffect(() => {
        async function checkCondition() {
            const adminStatus = await checkAdminStatus();
            setIsAdmin(adminStatus);
            console.log(adminStatus);
        }
        checkCondition();
    }, []);

  return (
    <div>
        <nav>
                <a id="navMenu" onClick={toggleMenu}>
                    <img id="settings" src={SettingsIcon} alt="Settings" onClick={toggleMenu} />
                </a>
                <img id="textlogo" src={TextLogo} alt="Logo" />
                <img id="logo" src={logo} alt="Logo" />

        </nav>

        <div className="navbar" id="myNavbar">
                <p>
                {username}
                </p>
                <Link to="/dashboard">Dashboard</Link>
                <Link to="/data">Przeglądaj Dane</Link>
                <Link to="/sharing">Udostepnianie</Link>
                <Link to="/Settings">Ustawienia</Link>
                {isAdmin === true && <Link to="/admin">AdminPanel</Link>}
                <Link to="/logout">Wyloguj</Link>
            </div>
    </div>
    )
}

export default Nav