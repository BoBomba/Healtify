import React from 'react';
import { Link } from 'react-router-dom';
import '../css/dashboard.css';


function toggleMenu() {
    var navbar = document.getElementById("myNavbar");
    navbar.style.transform = navbar.style.transform === "translateX(0)" ? "translateX(-101%)" : "translateX(0)";
}


function Dashboard() {
    document.addEventListener('click', function (event) {

        // Dodana obsługa zdarzenia na kliknięcie poza menu
        var navbar = document.getElementById("myNavbar");
        var button = document.querySelector('#navMenu');
    
        if (event.target !== navbar && event.target !== button) {
            // Jeśli kliknięto poza menu i przyciskiem, schowaj menu
            navbar.style.transform = "translateX(-101%)";
        }
    });
    return (
        <div className="dashboard">
            {/* {sessionStorage.getItem('incident_added') && (
                <script>
                    {() => {
                        window.onload = function() {
                            alert('Pomyślnie dodano zdarzenie');
                        };
                    }}
                </script>
            )}
            {sessionStorage.removeItem('incident_added')} */}

            <nav>
                <a id="navMenu" onClick={toggleMenu}>
                    |||
                </a>
                {/* TODO zrobic wyswietlanie zdjec */}
                <img id="textlogo" src="" alt="Logo" />
                <img id="logo" src="" alt="Logo" />
            </nav>

            <div className="navbar" id="myNavbar">
                <p>
                    {/* {sessionStorage.getItem('name') && sessionStorage.getItem('surname')
                        ? `${sessionStorage.getItem('name')} ${sessionStorage.getItem('surname')}`
                        : 'Niezalogowany'} */}
                    Niezalogowany
                </p>
                <Link to="/dashboard">Home</Link>

                <Link to="/new">Dodaj cos</Link>
                <Link to="/logout">Wyloguj</Link>
            </div>

            <main>
                <div id=""></div>
            </main>

            <footer>
                Damian Guca
            </footer>
        </div>
    );
}

export default Dashboard;
