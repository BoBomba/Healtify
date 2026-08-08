import React, { useEffect, useState } from 'react';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import Nav from '../../Components/Nav';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import {
    AcceptRequest,
    GetMyPatients,
    GetPendingRequests,
    InvitePatient,
    RejectRequest,
    SearchPatients,
} from '../../service/doctorService';
import { formatAppointmentDateTime } from '../../utils/appointmentUtils';

// Backend i tak odsiewa krótsze frazy - tu tylko nie zawracamy mu głowy.
const MIN_QUERY_LENGTH = 2;

// Co pokazać zamiast przycisku "Zaproś" przy kimś, kto już jest w relacji z lekarzem.
const STATUS_LABELS = {
    ACCEPTED: 'Już Twój pacjent',
    PENDING: 'Zaproszenie w toku',
};

/**
 * Udostepnianie po stronie lekarza: wyszukiwarka nowych pacjentów, lista przypisanych
 * i - na dole, osobno - wiszące zaproszenia. Zaproszenie może wyjść z obu stron,
 * ale dostęp powstaje dopiero, gdy druga strona je zaakceptuje.
 */
function DoctorSharing() {
    const { doctor } = useDoctorGuard();
    const [patients, setPatients] = useState([]);
    const [requests, setRequests] = useState([]);
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [searching, setSearching] = useState(false);
    const [searched, setSearched] = useState(false);
    const [message, setMessage] = useState('');

    const reload = () => {
        GetMyPatients()
            .then((data) => setPatients(data))
            .catch((error) => console.log(error));
        GetPendingRequests()
            .then((data) => setRequests(data))
            .catch((error) => console.log(error));
    };

    useEffect(() => {
        if (!doctor) return;
        reload();
    }, [doctor]);

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
            setResults(await SearchPatients(trimmed));
            setSearched(true);
        } catch (error) {
            console.log(error);
            setMessage('Nie udało się wyszukać pacjentów.');
        } finally {
            setSearching(false);
        }
    };

    const handleInvite = async (patient) => {
        try {
            await InvitePatient(patient.userId);
            // Wynik wyszukiwania od razu odzwierciedla nowy stan, żeby nie dało się
            // kliknąć "Zaproś" drugi raz zanim lista się odświeży.
            setResults((prev) => prev.map((item) =>
                item.userId === patient.userId
                    ? { ...item, status: 'PENDING', initiatedBy: 'DOCTOR' }
                    : item
            ));
            setMessage(`Zaproszenie wysłane do ${patient.username}.`);
            reload();
        } catch (error) {
            console.log(error);
            setMessage(error.response?.data?.message || 'Nie udało się wysłać zaproszenia.');
        }
    };

    const handleAccept = async (request) => {
        try {
            await AcceptRequest(request.sharingId);
            setMessage(`${request.patient.username} jest teraz Twoim pacjentem.`);
            reload();
        } catch (error) {
            console.log(error);
            setMessage('Nie udało się zaakceptować prośby.');
        }
    };

    const handleReject = async (request) => {
        try {
            await RejectRequest(request.sharingId);
            setMessage('Prośba odrzucona.');
            reload();
        } catch (error) {
            console.log(error);
            setMessage('Nie udało się odrzucić prośby.');
        }
    };

    const incoming = requests.filter((request) => request.initiatedBy === 'PATIENT');
    const outgoing = requests.filter((request) => request.initiatedBy === 'DOCTOR');

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="doctor-page">
                    <h2>Udostepnianie</h2>

                    {message && <div id="messages">{message}</div>}

                    <div className="doctor-columns">
                        <div className="datablock doctor-panel">
                            <h3>Znajdź nowego pacjenta</h3>
                            <form className="doctor-search" onSubmit={handleSearch}>
                                <input
                                    type="text"
                                    placeholder="Nazwa użytkownika lub e-mail"
                                    value={query}
                                    onChange={(e) => setQuery(e.target.value)}
                                />
                                <button type="submit" className="modal-btn primary small" disabled={searching}>
                                    {searching ? 'Szukam...' : 'Szukaj'}
                                </button>
                            </form>

                            {searched && results.length === 0 && <p>Brak pacjentów pasujących do wyszukiwania.</p>}

                            {results.map((result) => (
                                <div className="doctor-list-row" key={result.userId}>
                                    <div className="doctor-list-main">
                                        <strong>{result.username}</strong>
                                        <span className="doctor-list-sub">{result.email}</span>
                                    </div>
                                    {STATUS_LABELS[result.status] ? (
                                        <span className="doctor-badge">{STATUS_LABELS[result.status]}</span>
                                    ) : (
                                        <button
                                            type="button"
                                            className="modal-btn primary small"
                                            onClick={() => handleInvite(result)}
                                        >
                                            Zaproś
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>

                        <div className="datablock doctor-panel">
                            <h3>Twoi pacjenci</h3>
                            {patients.length === 0 && <p>Nie masz jeszcze przypisanych pacjentów.</p>}
                            {patients.map((patient) => (
                                <div className="doctor-list-row" key={patient.userId}>
                                    <div className="doctor-list-main">
                                        <strong>{patient.username}</strong>
                                        <span className="doctor-list-sub">{patient.email}</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="datablock doctor-panel doctor-pending">
                        <h3>Oczekujące zaproszenia</h3>

                        {incoming.length === 0 && outgoing.length === 0 && <p>Brak oczekujących zaproszeń.</p>}

                        {incoming.map((request) => (
                            <div className="doctor-list-row" key={request.sharingId}>
                                <div className="doctor-list-main">
                                    <strong>{request.patient.username}</strong>
                                    <span className="doctor-list-sub">
                                        prosi o opiekę - {formatAppointmentDateTime(request.requestSentDate)}
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
                                    <strong>{request.patient.username}</strong>
                                    <span className="doctor-list-sub">
                                        zaproszenie wysłane - {formatAppointmentDateTime(request.requestSentDate)}
                                    </span>
                                </div>
                                <span className="doctor-badge">Czeka na pacjenta</span>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
        </div>
    );
}

export default DoctorSharing;
