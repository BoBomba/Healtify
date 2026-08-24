import React, { useEffect, useState } from 'react';
import '../css/dashboard.css';
import '../css/doctor.css';
import Nav from '../Components/Nav';
import { validateToken } from '../service/authService';
import { getAdminStats } from '../service/adminService';

// Dashboard admina - sam rozmiar systemu w liczbach.
function AdminDashboard() {
    const [stats, setStats] = useState(null);
    const [loadError, setLoadError] = useState('');

    useEffect(() => {
        validateToken();
        getAdminStats()
            .then((data) => setStats(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać statystyk.');
            });
    }, []);

    const tiles = [
        { label: 'pacjentów', value: stats?.patients },
        { label: 'lekarzy', value: stats?.doctors },
        { label: 'powiązań pacjent-lekarz', value: stats?.sharings },
        { label: 'wizyt', value: stats?.appointments },
        { label: 'wpisów w dziennikach', value: stats?.journalEntries },
    ];

    return (
        <div className="dashboard">
            <Nav />

            <main>
                {/* Ten sam układ co pozostałe dashboardy: nagłówek sekcji + karta. */}
                <div className="main-container">
                    <h3>Panel administratora</h3>

                    {loadError && <div id="messages">{loadError}</div>}

                    <div className="datablock doctor-stats">
                        {tiles.map((tile) => (
                            <div className="doctor-stat" key={tile.label}>
                                <span className="doctor-stat-value">{tile.value ?? '-'}</span>
                                <span className="doctor-stat-label">{tile.label}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </main>

            <footer>Damian Guca</footer>
        </div>
    );
}

export default AdminDashboard;
