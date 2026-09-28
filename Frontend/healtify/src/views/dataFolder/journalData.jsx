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
import SearchBar from '../../Components/SearchBar';
import { useEffect, useMemo, useState } from 'react';
import { validateToken } from '../../service/authService';
import { DeleteJournalEntry, GetJournalEntries } from '../../service/dataService';
import { moodClass } from '../../utils/calendarUtils';
import { matchesDateRange, matchesQuery } from '../../utils/searchUtils';

const SCALE_FACES = ['😞', '🙁', '😐', '🙂', '😄'];

const EMPTY_FILTERS = { dateFrom: '', dateTo: '', mood: '', shared: 'all' };

// Lista jest ucinana - reszta dochodzi przyciskiem "Pokaż więcej".
const PAGE_SIZE = 10;

// Wszystkie wpisy pacjenta, od najnowszego.
function JournalData() {

  const [entries, setEntries] = useState([]);
  const [error, setError] = useState('');
  // Wpis otwarty do edycji - ten sam modal tylko z wypełnionymi polami.
  const [editingEntry, setEditingEntry] = useState(null);
  // Wpis otwarty w modalu udostępniania terapeucie.
  const [sharingEntry, setSharingEntry] = useState(null);
  const [query, setQuery] = useState('');
  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);

  const isShared = (entry) => (entry.sharedWithDoctorIds?.length ?? 0) > 0;

  const setFilter = (name, value) => setFilters((prev) => ({ ...prev, [name]: value }));

  const activeFilterCount = Object.keys(EMPTY_FILTERS)
    .filter((name) => filters[name] !== EMPTY_FILTERS[name]).length;

  const handleReset = () => {
    setQuery('');
    setFilters(EMPTY_FILTERS);
  };

  /** Po zapisie udostępnień podmieniamy wpis w miejscu */
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

  // Zmiana wyszukiwania zaczyna listę od nowa
  useEffect(() => setVisibleCount(PAGE_SIZE), [query, filters]);

  const filtered = useMemo(() => entries.filter((entry) => {
    // Data leci też do frazy, żeby dało się wpisać "2026-08" bez filtrów.
    const matchesText = matchesQuery(
      query,
      entry.title,
      entry.description,
      entry.symptoms ?? [],
      entry.entryAt.slice(0, 16).replace('T', ' ')
    );
    if (!matchesText) return false;
    if (!matchesDateRange(entry.entryAt, filters.dateFrom, filters.dateTo)) return false;
    if (filters.mood !== '' && String(entry.moodScale) !== filters.mood) return false;
    if (filters.shared === 'shared' && !isShared(entry)) return false;
    if (filters.shared === 'private' && isShared(entry)) return false;
    return true;
  }), [entries, query, filters]);

  const visible = filtered.slice(0, visibleCount);

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
              <h2>Dziennik</h2>
              <div className="datablock calendar-day-panel">
                {error && <div id="messages">{error}</div>}

                {!error && entries.length > 0 && (
                  <SearchBar
                    query={query}
                    onQueryChange={setQuery}
                    placeholder="Szukaj w tytule, opisie, objawach lub dacie..."
                    onReset={handleReset}
                    activeFilterCount={activeFilterCount}
                    summary={`Wpisy: ${visible.length} z ${filtered.length}${filtered.length !== entries.length ? ` (wszystkich: ${entries.length})` : ''}`}
                  >
                    <div className="search-field">
                      <label className="field-label" htmlFor="journal-date-from">Od dnia</label>
                      <input
                        id="journal-date-from"
                        type="date"
                        value={filters.dateFrom}
                        onChange={(e) => setFilter('dateFrom', e.target.value)}
                      />
                    </div>
                    <div className="search-field">
                      <label className="field-label" htmlFor="journal-date-to">Do dnia</label>
                      <input
                        id="journal-date-to"
                        type="date"
                        value={filters.dateTo}
                        onChange={(e) => setFilter('dateTo', e.target.value)}
                      />
                    </div>
                    <div className="search-field">
                      <label className="field-label" htmlFor="journal-mood">Samopoczucie</label>
                      <select
                        id="journal-mood"
                        value={filters.mood}
                        onChange={(e) => setFilter('mood', e.target.value)}
                      >
                        <option value="">Dowolne</option>
                        {SCALE_FACES.map((face, index) => (
                          <option value={index + 1} key={index}>{face} {index + 1}/5</option>
                        ))}
                      </select>
                    </div>
                    <div className="search-field">
                      <label className="field-label" htmlFor="journal-shared">Udostępnienie</label>
                      <select
                        id="journal-shared"
                        value={filters.shared}
                        onChange={(e) => setFilter('shared', e.target.value)}
                      >
                        <option value="all">Wszystkie</option>
                        <option value="shared">Udostępnione</option>
                        <option value="private">Nieudostępnione</option>
                      </select>
                    </div>
                  </SearchBar>
                )}

                {!error && entries.length === 0 && <p>Brak wpisów w dzienniku.</p>}
                {!error && entries.length > 0 && filtered.length === 0 && (
                  <p>Brak wpisów pasujących do wyszukiwania.</p>
                )}
                {visible.map((entry) => (
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

                {filtered.length > visible.length && (
                  <button
                    type="button"
                    className="modal-btn secondary small search-more"
                    onClick={() => setVisibleCount((prev) => prev + PAGE_SIZE)}
                  >
                    Pokaż więcej ({filtered.length - visible.length})
                  </button>
                )}

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
