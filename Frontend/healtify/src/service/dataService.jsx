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

export const GetSymptomsData = async () => {
    try {
        const response = await axios.get(`${API_URL}/symptoms`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetCalendarData = async () => {
    try {
        const response = await axios.get(`${API_URL}/calendar`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

// Zwraca listę wydarzeń kalendarza (wpisy pacjenta i wizyty u psychologa) dla zalogowanego użytkownika.
export const GetCalendarEvents = async () => {
    try {
        const response = await axios.get(`${API_URL}/calendar/events`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return [];
        }
        throw error;
    }
}

export const AddCalendarEvent = async (event) => {
    const response = await axios.post(`${API_URL}/calendar/events`, event, authConfig());
    return response.data;
}

export const GetMoodData = async () => {
    try {
        const response = await axios.get(`${API_URL}/mood`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSleepData = async () => {
    try {
        const response = await axios.get(`${API_URL}/sleep`, authConfig());
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
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

