import React from "react";
import { Link } from "react-router-dom";
import "../css/dashboard.css";
// styl listy wpisów (.day-event-item) mieszka razem z resztą stylów dziennika
import "../css/calendar.css";
// .doctor-panel - karta z listą wyrównaną do lewej, wspólna dla wszystkich dashboardów
import "../css/doctor.css";
// .big-btn - ten sam duży przycisk co w Ogólnych Danych.
import "../css/profile.css";
import Nav from "../Components/Nav";
import { useEffect, useState } from "react";
import { validateToken } from "../service/authService";
import {
  GetPatientProfile,
  GetJournalEntries,
} from "../service/dataService";
import PatientDetails from "../Components/PatientDetails";
import MoodAnalysis from "../Components/MoodAnalysis";
import AddEntryModal from "../Components/AddEntryModal";
import { moodClass } from "../utils/calendarUtils";

// Ile ostatnich wpisów pokazujemy - resztę widać w dzienniku.
const RECENT_ENTRIES_COUNT = 5;

function Dashboard() {
  const [profile, setProfile] = useState(null);
  // Komplet wpisów dla analizy nastroju, lista "Ostatnie wpisy" bierze z niego kilka pierwszych.
  const [entries, setEntries] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    validateToken();
    // Backend zawsze zwraca komplet pól - brakujące przychodzą jako null.
    GetPatientProfile()
      .then((fetchedProfile) => setProfile(fetchedProfile))
      .catch((error) => console.log(error));
    GetJournalEntries()
      .then((fetchedEntries) => setEntries(fetchedEntries))
      .catch((error) => console.log(error));
  }, []);

  // Sortujemy sami, bo wpis dodany z dashboardu może mieć wcześniejszą datę niż ostatni z listy.
  const recentEntries = [...entries]
    .sort((a, b) => b.entryAt.localeCompare(a.entryAt))
    .slice(0, RECENT_ENTRIES_COUNT);

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
            <button type="button" className="big-btn" onClick={() => setIsModalOpen(true)}>
              + Nowy wpis
            </button>
          </div>
        </div>

        <div className="main-container mood-container">
          <h3>Analiza nastroju</h3>
          <MoodAnalysis entries={entries} />
        </div>
      </main>

      <AddEntryModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSaved={(savedEntry) => setEntries((prev) => [...prev, savedEntry])}
      />

      <footer>Damian Guca</footer>
    </div>
  );
}

export default Dashboard;
