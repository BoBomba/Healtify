import axios from "axios";

const API_URL = 'http://localhost:8080/api/data';

export const GetGeneralData = async () => {
    try {
        const response = await axios.get(`${API_URL}/general`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            console.log("Brak danych dla użytkownika");
            return "Brak danych";
        }
        throw error;
    }
}

export const GetUserData = async () => {
    try {
        const response = await axios.get(`${API_URL}/user`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            console.log("Brak danych dla użytkownika");
            return "Brak danych";
        }
        throw error;
    }
}

/**
 * Szczegółowe dane pacjenta. Backend zawsze odpowiada 200 - gdy użytkownik nic jeszcze
 * nie wypełnił, wszystkie pola są nullem, a `completed` to false.
 */
export const GetPatientProfile = async () => {
    const response = await axios.get(`${API_URL}/profile`);
    return response.data;
}

/** Zapis całego formularza naraz (upsert po stronie backendu). */
export const SavePatientProfile = async (profile) => {
    const response = await axios.put(`${API_URL}/profile`, profile);
    return response.data;
}

// Wpisy dziennika zalogowanego pacjenta - backend zawsze filtruje po użytkowniku z tokenu,
// więc nie ma tu żadnego parametru z id użytkownika.
export const GetJournalEntries = async () => {
    const response = await axios.get(`${API_URL}/journal`);
    return Array.isArray(response.data) ? response.data : [];
}

export const AddJournalEntry = async (entry) => {
    const response = await axios.post(`${API_URL}/journal`, entry);
    return response.data;
}

/** Edycja wpisu - komplet pól, tak jak przy dodawaniu. */
export const UpdateJournalEntry = async (entryId, entry) => {
    const response = await axios.put(`${API_URL}/journal/${entryId}`, entry);
    return response.data;
}

export const DeleteJournalEntry = async (entryId) => {
    await axios.delete(`${API_URL}/journal/${entryId}`);
}

/**
 * Ustawia, którzy lekarze widzą ten wpis. Wysyłamy komplet zaznaczonych 
 * to samo nadaje i cofa dostęp.
 */
export const UpdateEntryShares = async (entryId, doctorIds) => {
    const response = await axios.put(`${API_URL}/journal/${entryId}/shares`, { doctorIds });
    return response.data;
}

export const GetSharingData = async () => {
    try {
        const response = await axios.get(`${API_URL}/sharing`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSettingsData = async () => {
    try {
        const response = await axios.get(`${API_URL}/settings`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}
