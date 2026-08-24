import React from "react";
import { Link } from "react-router-dom";
import "../css/dashboard.css";
// styl listy wpisów (.day-event-item) mieszka razem z resztą stylów dziennika
import "../css/calendar.css";
// .doctor-panel - karta z listą wyrównaną do lewej, wspólna dla wszystkich dashboardów
import "../css/doctor.css";
import Nav from "../Components/Nav";
import { useEffect, useState } from "react";
import { validateToken } from "../service/authService";
import {
  GetPatientProfile,
  GetJournalEntries,
} from "../service/dataService";
import PatientDetails from "../Components/PatientDetails";
import { moodClass } from "../utils/calendarUtils";

// Ile ostatnich wpisów pokazujemy na dashboardzie.
const RECENT_ENTRIES_COUNT = 3;

function Dashboard() {
  const [profile, setProfile] = useState(null);
  const [recentEntries, setRecentEntries] = useState([]);

  useEffect(() => {
    validateToken();
    // Backend zawsze zwraca komplet pól - brakujące przychodzą jako null.
    GetPatientProfile()
      .then((fetchedProfile) => setProfile(fetchedProfile))
      .catch((error) => console.log(error));
    GetJournalEntries()
      .then((entries) => {
        // Backend zwraca wpisy rosnąco po dacie - tu chcemy najświeższe.
        setRecentEntries([...entries].reverse().slice(0, RECENT_ENTRIES_COUNT));
      })
      .catch((error) => console.log(error));
  }, []);

  return (
    <div className="dashboard">
      <Nav />

      <main>
        <div className="main-container">
          <h3>Szczegółowe dane</h3>
          <div className="datablock">
            <PatientDetails profile={profile} />
            <Link to="/data/profile" className="big-btn secondary">Edytuj dane</Link>
          </div>
        </div>
        <div className="main-container">
          <h3>Ostatnie wpisy</h3>
          <div className="datablock doctor-panel">
            {recentEntries.length === 0 && <p>Brak wpisów w dzienniku.</p>}
            {recentEntries.map((entry) => (
              <div className="day-event-item" key={entry.entryId}>
                <div className="day-event-title">
                  <span className={`legend-dot ${moodClass(entry.moodScale)}`} />
                  <strong>{entry.title}</strong>
                  <span className="day-event-time">{entry.entryAt.slice(0, 16).replace('T', ' ')}</span>
                </div>
                <p>Samopoczucie: {entry.moodScale}/5</p>
              </div>
            ))}
          </div>
        </div>
      </main>

      <footer>Damian Guca</footer>
    </div>
  );
}

export default Dashboard;
