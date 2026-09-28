import axios from "axios";

const API_URL = 'http://localhost:8080/api/data';

const authConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
    }
});

export const GetGeneralData = async () => {
    try {
        const response = await axios.get(`${API_URL}/general`, authConfig());
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
        const response = await axios.get(`${API_URL}/user`, authConfig());
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
    const response = await axios.get(`${API_URL}/profile`, authConfig());
    return response.data;
}

/** Zapis całego formularza naraz (upsert po stronie backendu). */
export const SavePatientProfile = async (profile) => {
    const response = await axios.put(`${API_URL}/profile`, profile, authConfig());
    return response.data;
}

// Wpisy dziennika zalogowanego pacjenta - backend zawsze filtruje po użytkowniku z tokenu,
// więc nie ma tu żadnego parametru z id użytkownika.
export const GetJournalEntries = async () => {
    const response = await axios.get(`${API_URL}/journal`, authConfig());
    return Array.isArray(response.data) ? response.data : [];
}

export const AddJournalEntry = async (entry) => {
    const response = await axios.post(`${API_URL}/journal`, entry, authConfig());
    return response.data;
}

/** Edycja wpisu - komplet pól, tak jak przy dodawaniu. */
export const UpdateJournalEntry = async (entryId, entry) => {
    const response = await axios.put(`${API_URL}/journal/${entryId}`, entry, authConfig());
    return response.data;
}

export const DeleteJournalEntry = async (entryId) => {
    await axios.delete(`${API_URL}/journal/${entryId}`, authConfig());
}

/**
 * Ustawia, którzy lekarze widzą ten wpis. Wysyłamy komplet zaznaczonych 
 * to samo nadaje i cofa dostęp.
 */
export const UpdateEntryShares = async (entryId, doctorIds) => {
    const response = await axios.put(`${API_URL}/journal/${entryId}/shares`, { doctorIds }, authConfig());
    return response.data;
}

export const GetSharingData = async () => {
    try {
        const response = await axios.get(`${API_URL}/sharing`, authConfig());
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
        const response = await axios.get(`${API_URL}/settings`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}
