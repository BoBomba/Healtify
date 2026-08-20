
import React, { useEffect , useState} from 'react'
import { Link } from 'react-router-dom'
import '../css/dashboard.css'
import SettingsIcon from '../images/settings_icon.svg'
import TextLogo from '../images/Healtify_white.svg'
import { toggleMenu, useOutsideClick } from './Navbar';
import { getCurrentUser } from '../service/userService'
import logo from '../images/logo.png'

// Jedno menu dla obu ról - lekarz dostaje własny komplet linków (/doctor/*),
// bo jego widoki to osobne strony, a nie warianty widoków pacjenta.
function Nav() {
    useOutsideClick();
    const [currentUser, setCurrentUser] = useState(null);
    const username = currentUser ? currentUser.username : localStorage.getItem('username');

    useEffect(() => {
        async function loadCurrentUser() {
            try {
                // Sieć potrafi paść (np. backend nie działa) - menu ma się wtedy
                // wyrenderować bez linków ról, a nie wywalić całej strony.
                setCurrentUser(await getCurrentUser());
            } catch (error) {
                setCurrentUser(null);
            }
        }
        loadCurrentUser();
    }, []);

    const isDoctor = currentUser !== null && currentUser.doctor === true;
    const isAdmin = currentUser !== null && currentUser.admin === true;

  return (
    <div>
        <nav>
                {/* Uchwyt tylko na <a> - ten sam onClick na ikonie w środku przełączał
                    menu drugi raz (zdarzenie bąbelkuje), więc klik w samą ikonę
                    otwierał i od razu zamykał panel. */}
                <a id="navMenu" onClick={toggleMenu}>
                    <img id="settings" src={SettingsIcon} alt="Settings" />
                </a>
                <img id="textlogo" src={TextLogo} alt="Logo" />
                <img id="logo" src={logo} alt="Logo" />

        </nav>

        <div className="navbar" id="myNavbar">
                <p>
                {username}
                {/* Admin i lekarz to role nadane - pokazujemy je wprost. 
                "Pacjent" leci tylko wtedy, gdy nie ma żadnej z nich. */}
                {isAdmin && <span className="nav-role-badge admin">admin</span>}
                {isDoctor && <span className="nav-role-badge">lekarz</span>}
                {currentUser !== null && !isAdmin && !isDoctor && (
                    <span className="nav-role-badge patient">pacjent</span>
                )}
                </p>
                {isDoctor ? (
                    <>
                        <Link to="/doctor/dashboard">Dashboard</Link>
                        <Link to="/doctor/calendar">Kalendarz</Link>
                        <Link to="/doctor/data">Wizyty</Link>
                        <Link to="/doctor/sharing">Pacjenci</Link>
                        <Link to="/doctor/profile">Moje dane</Link>
                    </>
                ) : (
                    <>
                        <Link to="/dashboard">Dashboard</Link>
                        <Link to="/calendar">Kalendarz</Link>
                        <Link to="/data">Przeglądaj Dane</Link>
                        <Link to="/sharing">Lekarze</Link>
                    </>
                )}
                <Link to="/Settings">Ustawienia</Link>
                {isAdmin && <Link to="/admin">AdminPanel</Link>}
                <Link to="/logout">Wyloguj</Link>
            </div>
    </div>
    )
}

export default Nav
