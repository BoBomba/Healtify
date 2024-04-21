import React from 'react';
// import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';

function Dashboard() {
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
            <Nav />
            

            <main>
                <div className="main-container">1.</div>
                <div className="main-container">2.</div>
                <div className="main-container">3.</div>
                {/* TODO Dodać wiecej widoków */}
            </main>

            <footer>
                Damian Guca
            </footer>
        </div>
    );
}

export default Dashboard;
