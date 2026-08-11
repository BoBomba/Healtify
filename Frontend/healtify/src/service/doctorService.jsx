import axios from "axios";

const API_URL = 'http://localhost:8080/api/doctor';

const authConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
    }
});

// Backend filtruje wszystko po lekarzu z tokenu, więc nie ma id lekarza.

export const GetDoctorProfile = async () => {
    const response = await axios.get(`${API_URL}/me`, authConfig());
    return response.data;
}

export const GetMyPatients = async () => {
    const response = await axios.get(`${API_URL}/patients`, authConfig());
    return Array.isArray(response.data) ? response.data : [];
}

/** Wiszące zaproszenia w obie strony - rozdzielamy je po polu initiatedBy. */
export const GetPendingRequests = async () => {
    const response = await axios.get(`${API_URL}/requests`, authConfig());
    return Array.isArray(response.data) ? response.data : [];
}

export const SearchPatients = async (query) => {
    const response = await axios.get(`${API_URL}/patients/search`, {
        ...authConfig(),
        params: { q: query }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export const InvitePatient = async (patientId) => {
    const response = await axios.post(`${API_URL}/patients/${patientId}/invite`, {}, authConfig());
    return response.data;
}

/**
 * Zakończenie opieki nad pacjentem. Lekarz traci dostęp do jego danych, a umówione
 * wizyty tej pary znikają - terminy wracają do kalendarza. Da się odnowić zaproszeniem.
 */
export const RemovePatient = async (patientId) => {
    await axios.delete(`${API_URL}/patients/${patientId}`, authConfig());
}

export const AcceptRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/accept`, {}, authConfig());
    return response.data;
}

export const RejectRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/reject`, {}, authConfig());
    return response.data;
}

/** scope: 'upcoming' (domyślnie) albo 'all' - kalendarz potrzebuje też miesięcy wstecz. */
export const GetAppointments = async (scope = 'upcoming') => {
    const response = await axios.get(`${API_URL}/appointments`, {
        ...authConfig(),
        params: { scope }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export const AddAppointment = async (appointment) => {
    const response = await axios.post(`${API_URL}/appointments`, appointment, authConfig());
    return response.data;
}

export const DeleteAppointment = async (appointmentId) => {
    await axios.delete(`${API_URL}/appointments/${appointmentId}`, authConfig());
}
