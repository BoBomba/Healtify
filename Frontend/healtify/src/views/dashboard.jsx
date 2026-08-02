import React from "react";
import "../css/dashboard.css";
// styl listy wpisów (.day-event-item) mieszka razem z resztą stylów dziennika
import "../css/calendar.css";
import Nav from "../Components/Nav";
import { useEffect, useState } from "react";
import { validateToken } from "../service/authService";
import {
  GetGeneralData,
  GetJournalEntries,
} from "../service/dataService";
import { RenderData } from "../Components/RenderData";

// Ile ostatnich wpisów pokazujemy na dashboardzie.
const RECENT_ENTRIES_COUNT = 3;

function Dashboard() {
  const [generalData, setGeneralData] = useState(null);
  const [recentEntries, setRecentEntries] = useState([]);

  useEffect(() => {
    validateToken();
    GetGeneralData().then((fetchedData) => {
      console.log(fetchedData);
      if (fetchedData === "null") {
        setGeneralData(null);
      } else {
        setGeneralData(fetchedData);
      }
    });
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
          <div className="datablock">Ogólne Dane</div>
          <div className="datablock">
            {generalData && RenderData(generalData)}
          </div>
        </div>
        <div className="main-container">
          <div className="datablock">Ostatnie wpisy</div>
          <div className="datablock">
            {recentEntries.length === 0 && <p>Brak wpisów w dzienniku.</p>}
            {recentEntries.map((entry) => (
              <div className="day-event-item" key={entry.entryId}>
                <div className="day-event-title">
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
