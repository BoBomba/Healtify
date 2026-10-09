import axios from "axios";

const API_URL = 'http://localhost:8080/api/sharing';
const APPOINTMENTS_URL = 'http://localhost:8080/api/data/appointments';

// Strona pacjenta: to on decyduje, kto ma dostęp do jego danych.

export const GetMyDoctors = async () => {
    const response = await axios.get(`${API_URL}/doctors`);
    return Array.isArray(response.data) ? response.data : [];
}

export const GetPendingRequests = async () => {
    const response = await axios.get(`${API_URL}/requests`);
    return Array.isArray(response.data) ? response.data : [];
}

export const SearchDoctors = async (query) => {
    const response = await axios.get(`${API_URL}/doctors/search`, {
        params: { q: query }
    });
    return Array.isArray(response.data) ? response.data : [];
}

export const RequestDoctor = async (doctorId) => {
    const response = await axios.post(`${API_URL}/doctors/${doctorId}/request`, {});
    return response.data;
}

export const AcceptRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/accept`, {});
    return response.data;
}

export const RejectRequest = async (sharingId) => {
    const response = await axios.post(`${API_URL}/requests/${sharingId}/reject`, {});
    return response.data;
}

/** Cofnięcie zgody - lekarz traci dostęp do danych pacjenta. */
export const RevokeAccess = async (doctorId) => {
    await axios.delete(`${API_URL}/doctors/${doctorId}`);
}

/** Wizyty pacjenta - tylko do odczytu, zakłada je lekarz. */
export const GetMyAppointments = async () => {
    const response = await axios.get(APPOINTMENTS_URL);
    return Array.isArray(response.data) ? response.data : [];
}
