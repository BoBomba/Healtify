// Eksport wizyt do pliku .ics (RFC 5545) - generowany po Frontendzie,
// z wizyt juz wczytanych do kalendarza.

/** Wizyty nie maja w bazie dlugosci trwania */
const DEFAULT_DURATION_MINUTES = 60;

/** Znaki specjalne w tekscie ICS: \ ; , i zlamania linii. */
const escapeText = (value) => String(value ?? '')
    .replace(/\\/g, '\\\\')
    .replace(/;/g, '\\;')
    .replace(/,/g, '\\,')
    .replace(/\r?\n/g, '\\n');

const encoder = new TextEncoder();

/**
 * RFC 5545 dopuszcza max 75 oktetow w linii - reszte lamiemy ze spacja na poczatku.
 * Liczymy w oktetach i tniemy po pelnych znakach, zeby nie rozwalic UTF-8 (polskie znaki, emoji).
 */
const foldLine = (line) => {
    if (encoder.encode(line).length <= 75) return line;

    const parts = [];
    let current = '';
    let bytes = 0;
    // Pierwsza linia ma 75 oktetow, kolejne 74 - dla spacji.
    let limit = 75;

    for (const char of line) {
        const size = encoder.encode(char).length;
        if (bytes + size > limit) {
            parts.push(current);
            current = '';
            bytes = 0;
            limit = 74;
        }
        current += char;
        bytes += size;
    }
    parts.push(current);

    return parts.map((part, index) => (index === 0 ? part : ` ${part}`)).join('\r\n');
};

const pad = (value) => String(value).padStart(2, '0');

/** Czas lokalny ("floating") */
const toLocalStamp = (date) =>
    `${date.getFullYear()}${pad(date.getMonth() + 1)}${pad(date.getDate())}` +
    `T${pad(date.getHours())}${pad(date.getMinutes())}${pad(date.getSeconds())}`;

/** DTSTAMP musi byc w UTC. */
const toUtcStamp = (date) =>
    `${date.getUTCFullYear()}${pad(date.getUTCMonth() + 1)}${pad(date.getUTCDate())}` +
    `T${pad(date.getUTCHours())}${pad(date.getUTCMinutes())}${pad(date.getUTCSeconds())}Z`;

const doctorLabel = (doctor) =>
    [doctor?.title, doctor?.doctorName].filter(Boolean).join(' ') || 'lekarz';

/** Wizyty od dzisiaj w gore - liczony od polnocy */
export const filterUpcoming = (appointments) => {
    const startOfToday = new Date();
    startOfToday.setHours(0, 0, 0, 0);
    return appointments.filter((appointment) => {
        const at = new Date(appointment.appointmentAt);
        return !Number.isNaN(at.getTime()) && at >= startOfToday;
    });
};

/**
 * Buduje jedno VEVENT z wizyty. Role: pacjent/doctor.
 * pacjent widzi u kogo jest, lekarz - kogo przyjmuje.
 */
const buildEvent = (appointment, role) => {
    const start = new Date(appointment.appointmentAt);
    const end = new Date(start.getTime() + DEFAULT_DURATION_MINUTES * 60 * 1000);
    const doctor = appointment.doctor;

    const summary = role === 'doctor'
        ? `${appointment.title} - ${appointment.patient?.username ?? 'pacjent'}`
        : `Wizyta: ${appointment.title}`;

    const description = [
        role === 'doctor'
            ? `Pacjent: ${appointment.patient?.username ?? ''} (${appointment.patient?.email ?? ''})`
            : `Lekarz: ${doctorLabel(doctor)}${doctor?.specialization ? `, ${doctor.specialization}` : ''}`,
        appointment.notes
    ].filter(Boolean).join('\n');

    const location = [doctor?.workplace, doctor?.workAddress].filter(Boolean).join(', ');

    const lines = [
        'BEGIN:VEVENT',
        `UID:healtify-appointment-${appointment.appointmentId}@healtify`,
        `DTSTAMP:${toUtcStamp(new Date())}`,
        `DTSTART:${toLocalStamp(start)}`,
        `DTEND:${toLocalStamp(end)}`,
        `SUMMARY:${escapeText(summary)}`,
        `DESCRIPTION:${escapeText(description)}`
    ];
    if (location) {
        lines.push(`LOCATION:${escapeText(location)}`);
    }
    lines.push('END:VEVENT');
    return lines;
};

/** Caly plik .ics jako tekst. Wizyty bez poprawnej daty pomijamy. */
export const buildAppointmentsIcs = (appointments, role) => {
    const events = appointments
        .filter((appointment) => !Number.isNaN(new Date(appointment.appointmentAt).getTime()))
        .flatMap((appointment) => buildEvent(appointment, role));

    return [
        'BEGIN:VCALENDAR',
        'VERSION:2.0',
        'PRODID:-//Healtify//Kalendarz wizyt//PL',
        'CALSCALE:GREGORIAN',
        'METHOD:PUBLISH',
        ...events,
        'END:VCALENDAR'
    ].map(foldLine).join('\r\n') + '\r\n';
};

/** Zrzuca tekst .ics do pliku przez tymczasowy link. */
export const downloadIcs = (icsText, fileName) => {
    const blob = new Blob([icsText], { type: 'text/calendar;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
};
