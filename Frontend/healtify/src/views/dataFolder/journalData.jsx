import React from 'react'
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import Nav from '../../Components/Nav';
import { useEffect, useState } from 'react';
import { validateToken } from '../../service/authService';
import { GetJournalEntries } from '../../service/dataService';
import { moodClass } from '../../utils/calendarUtils';

// Wszystkie wpisy dziennika zalogowanego pacjenta, od najnowszego.
function JournalData() {

  const [entries, setEntries] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    validateToken();
    GetJournalEntries()
      .then((fetchedEntries) => setEntries([...fetchedEntries].reverse()))
      .catch((err) => {
        console.log(err);
        setError('Nie udało się pobrać wpisów.');
      });
  }, []);

  return (
    <div>

        <Nav />
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Dziennik</div>
              <div className="datablock calendar-day-panel">
                {error && <div id="messages">{error}</div>}
                {!error && entries.length === 0 && <p>Brak wpisów w dzienniku.</p>}
                {entries.map((entry) => (
                  <div className="day-event-item" key={entry.entryId}>
                    <div className="day-event-title">
                      <span className={`legend-dot ${moodClass(entry.moodScale)}`} />
                      <strong>{entry.title}</strong>
                      <span className="day-event-time">{entry.entryAt.slice(0, 16).replace('T', ' ')}</span>
                    </div>
                    <p>Samopoczucie: {entry.moodScale}/5</p>
                    {entry.symptoms && entry.symptoms.length > 0 && (
                      <div className="tag-list">
                        {entry.symptoms.map((symptom, i) => (
                          <span className={`tag-chip ${moodClass(entry.moodScale)}`} key={i}>{symptom}</span>
                        ))}
                      </div>
                    )}
                    {entry.description && <p>{entry.description}</p>}
                  </div>
                ))}
              </div>
            </div>
          </div>

        </main>
    </div>
  )
}

export default JournalData
