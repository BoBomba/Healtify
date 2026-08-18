import React, { useEffect, useMemo, useState } from 'react';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/calendar.css';
import '../../css/doctor.css';
import Nav from '../../Components/Nav';
import AddAppointmentModal from '../../Components/AddAppointmentModal';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { DeleteAppointment, GetAppointments, GetMyPatients } from '../../service/doctorService';
import { formatAppointmentTime, groupAppointmentsByDay } from '../../utils/appointmentUtils';
import { MONTH_NAMES } from '../../utils/calendarUtils';

// Ładny nagłówek dnia: "7 sierpnia 2026" z klucza "2026-08-07".
const formatDayHeading = (dayKey) => {
    const [year, month, day] = dayKey.split('-');
    return `${Number(day)} ${MONTH_NAMES[Number(month) - 1].toLowerCase()} ${year}`;
};

/**
 * Zakładka "Wizyty" u lekarza.
 * Świadomie pokazuje TYLKO przyszłe wizyty (backend filtruje po dacie),
 * pogrupowane w dni, żeby dało się to czytać jak grafik.
 */
function DoctorData() {
    const { doctor } = useDoctorGuard();
    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    // Wizyta otwarta do edycji - ten sam modal tylko z wypełnionymi polami.
    const [editingAppointment, setEditingAppointment] = useState(null);
    const [loadError, setLoadError] = useState('');

    useEffect(() => {
        if (!doctor) return;

        GetAppointments('upcoming')
            .then((data) => setAppointments(data))
            .catch((error) => {
                console.log(error);
                setLoadError('Nie udało się pobrać wizyt.');
            });
        // Lista pacjentów jest potrzebna modalowi edycji - 
        // wizytę wolno przepiąć tylko na innego dodanego pacjenta.
        GetMyPatients()
            .then((data) => setPatients(data))
            .catch((error) => console.log(error));
    }, [doctor]);

    /**
     * Ta strona pokazuje tylko przyszłe wizyty, więc przesunięcie wizyty w przeszłość
     * usuwa ją z listy - inaczej wisiałaby tu wbrew nagłówkowi.
     */
    const handleAppointmentSaved = (saved) => {
        setAppointments((prev) => {
            const updated = prev
                .map((item) => (item.appointmentId === saved.appointmentId ? saved : item))
                .filter((item) => new Date(item.appointmentAt) >= new Date());
            return updated.sort((a, b) => a.appointmentAt.localeCompare(b.appointmentAt));
        });
    };

    const days = useMemo(() => {
        const grouped = groupAppointmentsByDay(appointments);
        return Object.keys(grouped).sort().map((dayKey) => ({ dayKey, items: grouped[dayKey] }));
    }, [appointments]);

    const handleDelete = async (appointment) => {
        const confirmed = window.confirm(
            `Odwołać wizytę "${appointment.title}" z pacjentem ${appointment.patient.username}?`
        );
        if (!confirmed) return;

        try {
            await DeleteAppointment(appointment.appointmentId);
            setAppointments((prev) =>
                prev.filter((item) => item.appointmentId !== appointment.appointmentId)
            );
        } catch (error) {
            console.log(error);
            setLoadError('Nie udało się odwołać wizyty.');
        }
    };

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="doctor-page">
                    <h2>Wszystkie przyszłe wizyty</h2>

                    {loadError && <div id="messages">{loadError}</div>}

                    <div className="datablock doctor-panel">
                        {appointments.length === 0 && <p>Brak zaplanowanych wizyt.</p>}

                        {days.map(({ dayKey, items }) => (
                            <div className="doctor-day-group" key={dayKey}>
                                <h3 className="doctor-day-heading">{formatDayHeading(dayKey)}</h3>
                                {items.map((appointment) => (
                                    <div className="day-event-item" key={appointment.appointmentId}>
                                        <div className="day-event-title">
                                            <span className="legend-dot appointment" />
                                            <strong>{appointment.title}</strong>
                                            <span className="day-event-time">
                                                {formatAppointmentTime(appointment.appointmentAt)}
                                            </span>
                                            <div className="day-event-actions">
                                                <button
                                                    type="button"
                                                    className="modal-btn secondary small"
                                                    onClick={() => setEditingAppointment(appointment)}
                                                >
                                                    Edytuj
                                                </button>
                                                <button
                                                    type="button"
                                                    className="modal-btn danger small"
                                                    onClick={() => handleDelete(appointment)}
                                                >
                                                    Odwołaj
                                                </button>
                                            </div>
                                        </div>
                                        <p>Pacjent: {appointment.patient.username} ({appointment.patient.email})</p>
                                        {appointment.notes && <p>{appointment.notes}</p>}
                                    </div>
                                ))}
                            </div>
                        ))}
                    </div>
                </div>
            </main>

            {/* Ta strona tylko edytuje - nowe wizyty zakłada się z kalendarza. */}
            <AddAppointmentModal
                isOpen={editingAppointment !== null}
                onClose={() => setEditingAppointment(null)}
                onSaved={handleAppointmentSaved}
                patients={patients}
                appointment={editingAppointment}
            />
        </div>
    );
}

export default DoctorData;
