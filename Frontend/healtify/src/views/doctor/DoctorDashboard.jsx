import React, { useEffect, useState } from 'react';
import '../../css/dashboard.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import Nav from '../../Components/Nav';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { GetAppointments, GetMyPatients, GetPendingRequests } from '../../service/doctorService';
import { formatAppointmentDateTime } from '../../utils/appointmentUtils';

// Ile najbliższych wizyt mieści się na dashboardzie - pełna lista jest w zakładce Wizyty.
const UPCOMING_COUNT = 4;

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
            .then((data) => setAppointments(data.slice(0, UPCOMING_COUNT)))
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
                            <span className="doctor-stat-value">{pendingCount}</span>
                            <span className="doctor-stat-label">oczekujących zaproszeń</span>
                        </div>
                    </div>
                </div>

                <div className="main-container">
                    <h3>Najbliższe wizyty</h3>
                    <div className="datablock doctor-panel">
                        {loadError && <div id="messages">{loadError}</div>}
                        {!loadError && appointments.length === 0 && <p>Brak zaplanowanych wizyt.</p>}
                        {appointments.map((appointment) => (
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
