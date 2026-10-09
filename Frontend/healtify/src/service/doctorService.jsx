import axios from "axios";

const API_URL = 'http://localhost:8080/api/doctor';

// Backend filtruje wszystko po lekarzu z tokenu, więc nie ma id lekarza.

export const GetDoctorProfile = async () => {
    const response = await axios.get(`${API_URL}/me`);
    return response.data;
}

/**
 * Zapis własnych danych lekarza. Wiersz w doctors zakłada admin przy nadaniu roli,
 * więc zawsze update - on oznacza profil jako uzupełniony.
 */
export const SaveDoctorProfile = async (profile) => {
    const response = await axios.put(`${API_URL}/profile`, profile);
    return response.data;
}

export const GetMyPatients = async () => {
    const response = await axios.get(`${API_URL}/patients`);
    return Array.isArray(response.data) ? response.data : [];
}

/**
 * Szczegółowe dane pacjenta. Backend wpuszcza tu tylko przy ACCEPTED,
 * więc bez zgody pacjenta poleci 403.
 */
export const GetPatientProfile = async (patientId) => {
    const response = await axios.get(`${API_URL}/patients/${patientId}/profile`);
    return response.data;
}

/**
 * Wpisy z dziennika, które pacjent udostępnił temu lekarzowi. 
 * Wyłącznie te z wierszem w journal_entry_shares.
 */
export const GetPatientJournal = async (patientId) => {
    const response = await axios.get(`${API_URL}/patients/${patientId}/journal`);
    return Array.isArray(response.data) ? response.data : [];
}

/** Wiszące zaproszenia w obie strony - rozdzielamy je po polu initiatedBy. */
export const GetPendingRequests = async () => {
    const response = await axios.get(`${API_URL}/requests`);
    return Array.isArray(response.data) ? response.data : [];
}

export const SearchPatients = async (query) => {
    const response = await axios.get(`${API_URL}/patients/search`, {
        params: { q: query }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export const InvitePatient = async (patientId) => {
    const response = await axios.post(`${API_URL}/patients/${patientId}/invite`, {});
    return response.data;
}

/**
 * Zakończenie opieki nad pacjentem. Lekarz traci dostęp do danych, a umówione
 * wizyty znikają - terminy wracają do kalendarza. Da się odnowić zaproszeniem.
 */
export const RemovePatient = async (patientId) => {
    await axios.delete(`${API_URL}/patients/${patientId}`);
}

export const AcceptRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/accept`, {});
    return response.data;
}

export const RejectRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/reject`, {});
    return response.data;
}

/** scope: 'upcoming' (domyślnie) albo 'all' - kalendarz potrzebuje też miesięcy wstecz. */
export const GetAppointments = async (scope = 'upcoming') => {
    const response = await axios.get(`${API_URL}/appointments`, {
        params: { scope }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export const AddAppointment = async (appointment) => {
    const response = await axios.post(`${API_URL}/appointments`, appointment);
    return response.data;
}

/** Edycja wizyty - komplet pól, tak jak przy zakładaniu. */
export const UpdateAppointment = async (appointmentId, appointment) => {
    const response = await axios.put(`${API_URL}/appointments/${appointmentId}`, appointment);
    return response.data;
}

export const DeleteAppointment = async (appointmentId) => {
    await axios.delete(`${API_URL}/appointments/${appointmentId}`);
}
