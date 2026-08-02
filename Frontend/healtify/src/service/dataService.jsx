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

// Wpisy dziennika zalogowanego pacjenta - backend zawsze filtruje po użytkowniku z tokenu,
// więc nie ma tu (i nie może być) żadnego parametru z id użytkownika.
export const GetJournalEntries = async () => {
    const response = await axios.get(`${API_URL}/journal`, authConfig());
    return Array.isArray(response.data) ? response.data : [];
}

export const AddJournalEntry = async (entry) => {
    const response = await axios.post(`${API_URL}/journal`, entry, authConfig());
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
