import React, { useEffect, useState } from 'react';
import '../../css/dashboard.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import Nav from '../../Components/Nav';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { GetAppointments, GetMyPatients, GetPendingRequests } from '../../service/doctorService';
import { appointmentDayKey, formatAppointmentDateTime } from '../../utils/appointmentUtils';
import { formatDateKey } from '../../utils/calendarUtils';

// Ile najbliższych wizyt mieści się na dashboardzie - pełna lista jest w zakładce Wizyty.
const UPCOMING_COUNT = 6;

/** Klucz dnia oddalonego o `days` od dzisiaj - do progu "w tym tygodniu". */
const dayKeyFromToday = (days) => {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return formatDateKey(date);
};

// Dashboard lekarza nie ma nic wspólnego z danymi psychicznymi - lekarz ich u siebie
// nie zbiera. Zamiast tego patrzy do przodu: kto i kiedy do niego przychodzi.
function DoctorDashboard() {
    const { doctor } = useDoctorGuard();
    const [appointments, setAppointments] = useState([]);
    const [patientCount, setPatientCount] = useState(0);
    const [pendingCount, setPendingCount] = useState(0);
    const [loadError, setLoadError] = useState('');

    useEffect(() => {
        if (!doctor) return;

        GetAppointments('upcoming')
            .then((data) => setAppointments(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wizyt.');
            });
        GetMyPatients()
            .then((data) => setPatientCount(data.length))
            .catch((error) => console.log(error));
        GetPendingRequests()
            .then((data) => setPendingCount(data.length))
            .catch((error) => console.log(error));
    }, [doctor]);

    // Backend zwraca wizyty rosnąco, więc pierwsze z brzegu są najbliższe.
    const upcoming = appointments.slice(0, UPCOMING_COUNT);
    const todayKey = dayKeyFromToday(0);
    const weekKey = dayKeyFromToday(6);
    const todayCount = appointments.filter((a) => appointmentDayKey(a.appointmentAt) === todayKey).length;
    const weekCount = appointments.filter((a) => appointmentDayKey(a.appointmentAt) <= weekKey).length;

    return (
        <div className="dashboard">
            <Nav />

            <main>
                <div className="main-container">
                    <h3>
                        {doctor ? doctor.doctorName : 'Panel lekarza'}
                        {doctor && doctor.specialization && (
                            <span className="doctor-subtitle">{doctor.specialization}</span>
                        )}
                    </h3>
                    <div className="datablock doctor-stats">
                        <div className="doctor-stat">
                            <span className="doctor-stat-value">{patientCount}</span>
                            <span className="doctor-stat-label">pacjentów</span>
                        </div>
                        <div className="doctor-stat">
                            <span className="doctor-stat-value">{todayCount}</span>
                            <span className="doctor-stat-label">wizyt dziś</span>
                        </div>
                        <div className="doctor-stat">
                            <span className="doctor-stat-value">{weekCount}</span>
                            <span className="doctor-stat-label">wizyt w tym tygodniu</span>
                        </div>
                        <div className="doctor-stat">
                            <span className="doctor-stat-value">{appointments.length}</span>
                            <span className="doctor-stat-label">nadchodzących wizyt</span>
                        </div>
                        <div className="doctor-stat">
                            <span className="doctor-stat-value">{pendingCount}</span>
                            <span className="doctor-stat-label">oczekujących zaproszeń</span>
                        </div>
                    </div>
                </div>

                <div className="main-container">
                    <h3>Najbliższe wizyty</h3>
                    <div className="datablock doctor-panel">
                        {loadError && <div id="messages">{loadError}</div>}
                        {!loadError && upcoming.length === 0 && <p>Brak zaplanowanych wizyt.</p>}
                        {upcoming.map((appointment) => (
                            <div className="day-event-item" key={appointment.appointmentId}>
                                <div className="day-event-title">
                                    <span className="legend-dot appointment" />
                                    <strong>{appointment.title}</strong>
                                    <span className="day-event-time">
                                        {formatAppointmentDateTime(appointment.appointmentAt)}
                                    </span>
                                </div>
                                <p>Pacjent: {appointment.patient.username}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </main>

            <footer>Damian Guca</footer>
        </div>
    );
}

export default DoctorDashboard;
