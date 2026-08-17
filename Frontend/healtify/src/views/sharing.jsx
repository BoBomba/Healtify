import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import '../css/dashboard.css';
import '../css/data.css';
import '../css/calendar.css';
import '../css/doctor.css';
import '../css/chat.css';
import Nav from '../Components/Nav';
import { useConversations } from '../utils/useConversations';
import { validateToken } from '../service/authService';
import {
  AcceptRequest,
  GetMyDoctors,
  GetPendingRequests,
  RejectRequest,
  RequestDoctor,
  RevokeAccess,
  SearchDoctors,
} from '../service/sharingService';
import { formatAppointmentDateTime } from '../utils/appointmentUtils';

const MIN_QUERY_LENGTH = 2;

const STATUS_LABELS = {
  ACCEPTED: 'Ma dostęp do Twoich danych',
  PENDING: 'Prośba w toku',
};

/**
 * Udostepnianie po stronie pacjenta - druga polowa panelu lekarza.
 * To pacjent decyduje, kto widzi jego dane: sam prosi lekarza o opiekę
 * albo odpowiada na zaproszenie i w każdej chwili może cofnąć zgodę.
 */
function Sharing() {
  const [doctors, setDoctors] = useState([]);
  const [requests, setRequests] = useState([]);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const [searched, setSearched] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  // Listy lekarzy znaja tylko doctorId - sharingId potrzebne do czatu (i licznik
  // nieprzeczytanych) przychodzi osobno, razem z rozmowami tego konta.
  const { byPartner, refresh: reloadConversations, clearUnread } = useConversations('PATIENT');

  const reload = () => {
    GetMyDoctors()
      .then((data) => setDoctors(data))
      .catch((error) => console.log(error));
    GetPendingRequests()
      .then((data) => setRequests(data))
      .catch((error) => console.log(error));
    reloadConversations();
  };

  const openChat = (conversation) => {
    clearUnread(conversation.sharingId);
    navigate(`/sharing/chat/${conversation.sharingId}`);
  };

  useEffect(() => {
    validateToken();
    reload();
  }, []);

  const handleSearch = async (event) => {
    event.preventDefault();
    const trimmed = query.trim();
    if (trimmed.length < MIN_QUERY_LENGTH) {
      setMessage('Wpisz co najmniej 2 znaki.');
      return;
    }

    setSearching(true);
    setMessage('');
    try {
      setResults(await SearchDoctors(trimmed));
      setSearched(true);
    } catch (error) {
      console.log(error);
      setMessage('Nie udało się wyszukać lekarzy.');
    } finally {
      setSearching(false);
    }
  };

  const handleRequest = async (doctor) => {
    try {
      await RequestDoctor(doctor.doctorId);
      setResults((prev) => prev.map((item) =>
        item.doctorId === doctor.doctorId
          ? { ...item, status: 'PENDING', initiatedBy: 'PATIENT' }
          : item
      ));
      setMessage(`Prośba wysłana do ${doctor.doctorName}.`);
      reload();
    } catch (error) {
      console.log(error);
      setMessage(error.response?.data?.message || 'Nie udało się wysłać prośby.');
    }
  };

  const handleAccept = async (request) => {
    try {
      await AcceptRequest(request.sharingId);
      setMessage(`${request.doctor.doctorName} ma teraz dostęp do Twoich danych.`);
      reload();
    } catch (error) {
      console.log(error);
      setMessage('Nie udało się zaakceptować zaproszenia.');
    }
  };

  const handleReject = async (request) => {
    try {
      await RejectRequest(request.sharingId);
      setMessage('Zaproszenie odrzucone.');
      reload();
    } catch (error) {
      console.log(error);
      setMessage('Nie udało się odrzucić zaproszenia.');
    }
  };

  const handleRevoke = async (doctor) => {
    const confirmed = window.confirm(
      `Cofnąć dostęp do Twoich danych dla ${doctor.doctorName}?`
    );
    if (!confirmed) return;

    try {
      await RevokeAccess(doctor.doctorId);
      setMessage('Dostęp cofnięty.');
      reload();
    } catch (error) {
      console.log(error);
      setMessage('Nie udało się cofnąć dostępu.');
    }
  };

  // Zaproszenie od lekarza czeka na decyzję pacjenta i wice wersa.
  const incoming = requests.filter((request) => request.initiatedBy === 'DOCTOR');
  const outgoing = requests.filter((request) => request.initiatedBy === 'PATIENT');

  return (
    <div className="dashboard">
      <Nav />
      <main>
        <div className="doctor-page">
          <h2>Udostepnianie</h2>

          {message && <div id="messages">{message}</div>}

          <div className="doctor-columns">
            <div className="datablock doctor-panel">
              <h3>Znajdź lekarza</h3>
              <form className="doctor-search" onSubmit={handleSearch}>
                <input
                  type="text"
                  placeholder="Nazwisko, specjalizacja lub nazwa konta"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
                <button type="submit" className="modal-btn primary small" disabled={searching}>
                  {searching ? 'Szukam...' : 'Szukaj'}
                </button>
              </form>

              {searched && results.length === 0 && <p>Brak lekarzy pasujących do wyszukiwania.</p>}

              {results.map((result) => (
                <div className="doctor-list-row" key={result.doctorId}>
                  <div className="doctor-list-main">
                    <strong>{result.doctorName}</strong>
                    <span className="doctor-list-sub">
                      {result.specialization || 'Brak specjalizacji'}
                    </span>
                  </div>
                  {STATUS_LABELS[result.status] ? (
                    <span className="doctor-badge">{STATUS_LABELS[result.status]}</span>
                  ) : (
                    <button
                      type="button"
                      className="modal-btn primary small"
                      onClick={() => handleRequest(result)}
                    >
                      Poproś o opiekę
                    </button>
                  )}
                </div>
              ))}
            </div>

            <div className="datablock doctor-panel">
              <h3>Twoi lekarze</h3>
              {doctors.length === 0 && <p>Żaden lekarz nie ma dostępu do Twoich danych.</p>}
              {doctors.map((doctor) => {
                const conversation = byPartner.get(doctor.doctorId);
                return (
                  <div className="doctor-list-row" key={doctor.doctorId}>
                    <div className="doctor-list-main">
                      <strong>{doctor.doctorName}</strong>
                      <span className="doctor-list-sub">
                        {doctor.specialization || 'Brak specjalizacji'}
                      </span>
                    </div>
                    <div className="doctor-list-actions">
                      <button
                        type="button"
                        className="modal-btn primary small"
                        onClick={() => openChat(conversation)}
                        disabled={!conversation}
                      >
                        Czat
                        {conversation?.unreadCount > 0 && (
                          <span className="chat-unread">{conversation.unreadCount}</span>
                        )}
                      </button>
                      <button
                        type="button"
                        className="modal-btn secondary small"
                        onClick={() => handleRevoke(doctor)}
                      >
                        Cofnij dostęp
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="datablock doctor-panel doctor-pending">
            <h3>Oczekujące zaproszenia</h3>

            {incoming.length === 0 && outgoing.length === 0 && <p>Brak oczekujących zaproszeń.</p>}

            {incoming.map((request) => (
              <div className="doctor-list-row" key={request.sharingId}>
                <div className="doctor-list-main">
                  <strong>{request.doctor.doctorName}</strong>
                  <span className="doctor-list-sub">
                    zaprasza Cię jako pacjenta - {formatAppointmentDateTime(request.requestSentDate)}
                  </span>
                </div>
                <div className="doctor-list-actions">
                  <button
                    type="button"
                    className="modal-btn primary small"
                    onClick={() => handleAccept(request)}
                  >
                    Akceptuj
                  </button>
                  <button
                    type="button"
                    className="modal-btn secondary small"
                    onClick={() => handleReject(request)}
                  >
                    Odrzuć
                  </button>
                </div>
              </div>
            ))}

            {outgoing.map((request) => (
              <div className="doctor-list-row" key={request.sharingId}>
                <div className="doctor-list-main">
                  <strong>{request.doctor.doctorName}</strong>
                  <span className="doctor-list-sub">
                    prośba wysłana - {formatAppointmentDateTime(request.requestSentDate)}
                  </span>
                </div>
                <span className="doctor-badge">Czeka na lekarza</span>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}

export default Sharing
