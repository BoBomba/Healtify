// Wspólne formatowanie wizyt - używane przez dashboard, listę wizyt i oba kalendarze.

/** "2026-08-07T12:00:00" -> "2026-08-07 12:00" (bez parsowania do Date, żeby nie ruszać strefy). */
export const formatAppointmentDateTime = (appointmentAt) =>
    appointmentAt ? appointmentAt.slice(0, 16).replace('T', ' ') : '';

export const formatAppointmentTime = (appointmentAt) =>
    appointmentAt ? appointmentAt.slice(11, 16) : '';

/** Klucz dnia zgodny z formatDateKey z calendarUtils. */
export const appointmentDayKey = (appointmentAt) =>
    appointmentAt ? appointmentAt.slice(0, 10) : '';

/** Wizyty pogrupowane w dni, w każdym dniu posortowane po godzinie. */
export const groupAppointmentsByDay = (appointments) => {
    const map = {};
    appointments.forEach((appointment) => {
        const key = appointmentDayKey(appointment.appointmentAt);
        if (key === '') return;
        if (!map[key]) map[key] = [];
        map[key].push(appointment);
    });
    Object.values(map).forEach((dayAppointments) =>
        dayAppointments.sort((a, b) => a.appointmentAt.localeCompare(b.appointmentAt))
    );
    return map;
};
