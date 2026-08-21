import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Nav from '../../Components/Nav';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { GetMyPatients, GetPatientJournal } from '../../service/doctorService';
import { moodClass } from '../../utils/calendarUtils';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import '../../css/profile.css';

/**
 * Wpisy z dziennika, które pacjent udostępnił temu lekarzowi.
 * Widok jest celowo ten sam co u pacjenta w Dzienniku tylko bez przycisków.
 */
function DoctorPatientJournal() {
    const { patientId } = useParams();
    const navigate = useNavigate();
    const { checking } = useDoctorGuard();

    const [patient, setPatient] = useState(null);
    const [entries, setEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (checking) return;

        // Nazwa pacjenta nie przychodzi z listy wpisów, więc bierzemy ją z listy pacjentów -
        // ta i tak jest ograniczona do powiązań ACCEPTED.
        Promise.all([GetPatientJournal(patientId), GetMyPatients()])
            .then(([fetchedEntries, patients]) => {
                setEntries(fetchedEntries);
                setPatient(patients.find((item) => String(item.userId) === String(patientId)) ?? null);
            })
            .catch((err) => {
                console.log(err);
                setError(
                    err.response?.status === 403
                        ? 'Ten pacjent nie jest już pod Twoją opieką.'
                        : 'Nie udało się pobrać wpisów.'
                );
            })
            .finally(() => setLoading(false));
    }, [checking, patientId]);

    if (checking) {
        return (
            <div className="dashboard">
                <Nav />
                <main><p>Sprawdzanie uprawnień...</p></main>
            </div>
        );
    }

    const patientLabel = patient ? (patient.fullName || patient.username) : 'pacjenta';

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="block-container">
                    <div className="block-row">
                        {/* Ta sama klasa co w Dzienniku pacjenta */}
                        <div className="datablock calendar-day-panel">
                            <h2 className="profile-title">Wpisy - {patientLabel}</h2>

                            {loading && <p>Wczytywanie...</p>}
                            {error && <div id="messages">{error}</div>}

                            {!loading && !error && entries.length === 0 && (
                                <p>Ten pacjent nie udostępnił Ci jeszcze żadnych wpisów.</p>
                            )}

                            {entries.map((entry) => (
                                <div className="day-event-item" key={entry.entryId}>
                                    <div className="day-event-title">
                                        <span className={`legend-dot ${moodClass(entry.moodScale)}`} />
                                        <strong>{entry.title}</strong>
                                        <span className="day-event-time">
                                            {entry.entryAt.slice(0, 16).replace('T', ' ')}
                                        </span>
                                    </div>
                                    <p>Samopoczucie: {entry.moodScale}/5</p>
                                    {entry.symptoms && entry.symptoms.length > 0 && (
                                        <div className="tag-list">
                                            {entry.symptoms.map((symptom, i) => (
                                                <span className={`tag-chip ${moodClass(entry.moodScale)}`} key={i}>
                                                    {symptom}
                                                </span>
                                            ))}
                                        </div>
                                    )}
                                    {entry.description && <p>{entry.description}</p>}
                                </div>
                            ))}

                            <button
                                type="button"
                                className="big-btn secondary"
                                onClick={() => navigate('/doctor/sharing')}
                            >
                                Powrót
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default DoctorPatientJournal;
