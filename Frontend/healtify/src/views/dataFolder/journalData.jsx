import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
// .big-btn - ten sam duży przycisk co w Ogólnych Danych.
import '../../css/profile.css';
import Nav from '../../Components/Nav';
import AddEntryModal from '../../Components/AddEntryModal';
import ShareEntryModal from '../../Components/ShareEntryModal';
import { useEffect, useState } from 'react';
import { validateToken } from '../../service/authService';
import { DeleteJournalEntry, GetJournalEntries } from '../../service/dataService';
import { moodClass } from '../../utils/calendarUtils';

// Wszystkie wpisy dziennika zalogowanego pacjenta, od najnowszego.
function JournalData() {

  const [entries, setEntries] = useState([]);
  const [error, setError] = useState('');
  // Wpis otwarty do edycji - ten sam modal tylko z wypełnionymi polami.
  const [editingEntry, setEditingEntry] = useState(null);
  // Wpis otwarty w modalu udostępniania terapeucie.
  const [sharingEntry, setSharingEntry] = useState(null);

  const isShared = (entry) => (entry.sharedWithDoctorIds?.length ?? 0) > 0;

  /** Po zapisie udostępnień podmieniamy wpis w miejscu - kolejność się nie zmienia. */
  const handleSharesSaved = (savedEntry) => {
    setEntries((prev) =>
      prev.map((item) => (item.entryId === savedEntry.entryId ? savedEntry : item))
    );
    setSharingEntry(null);
  };

  useEffect(() => {
    validateToken();
    GetJournalEntries()
      .then((fetchedEntries) => setEntries([...fetchedEntries].reverse()))
      .catch((err) => {
        console.log(err);
        setError('Nie udało się pobrać wpisów.');
      });
  }, []);

  // Lista jest od najnowszego, więc po edycji podmieniamy wpis w miejscu
  // i przywracamy kolejność - zmiana daty może przesunąć go w czasie.
  const handleEntrySaved = (savedEntry) => {
    setEntries((prev) => {
      const updated = prev.map((item) => (item.entryId === savedEntry.entryId ? savedEntry : item));
      return updated.sort((a, b) => b.entryAt.localeCompare(a.entryAt));
    });
  };

  const handleDelete = async (entry) => {
    const confirmed = window.confirm(
      `Usunąć wpis "${entry.title}"?\n\nTej operacji nie da się cofnąć.`
    );
    if (!confirmed) return;

    try {
      await DeleteJournalEntry(entry.entryId);
      setEntries((prev) => prev.filter((item) => item.entryId !== entry.entryId));
    } catch (err) {
      console.log(err);
      setError('Nie udało się usunąć wpisu.');
    }
  };

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
                      <div className="day-event-actions">
                        <button
                          type="button"
                          className={`modal-btn share small${isShared(entry) ? ' active' : ''}`}
                          onClick={() => setSharingEntry(entry)}
                        >
                          {isShared(entry) ? 'Udostępniony' : 'Udostępnij'}
                        </button>
                        <button
                          type="button"
                          className="modal-btn secondary small"
                          onClick={() => setEditingEntry(entry)}
                        >
                          Edytuj
                        </button>
                        <button
                          type="button"
                          className="modal-btn danger small"
                          onClick={() => handleDelete(entry)}
                        >
                          Usuń
                        </button>
                      </div>
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
                <Link to="/data" className="big-btn secondary">Powrót</Link>
              </div>
            </div>
          </div>

        </main>

        {/* Strona tylko edytuje - nowe wpisy z kalendarza. */}
        <AddEntryModal
          isOpen={editingEntry !== null}
          onClose={() => setEditingEntry(null)}
          onSaved={handleEntrySaved}
          entry={editingEntry}
        />

        <ShareEntryModal
          isOpen={sharingEntry !== null}
          onClose={() => setSharingEntry(null)}
          onSaved={handleSharesSaved}
          entry={sharingEntry}
        />
    </div>
  )
}

export default JournalData
