import axios from "axios";

const API_URL = 'http://localhost:8080/api/data';

export const GetGeneralData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/general?token=${token}`);
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
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/user?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            console.log("Brak danych dla użytkownika");
            return "Brak danych";
        }
        throw error;
    }
}

export const GetHeartData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/heart?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSymptomsData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/symptoms?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetCalendarData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/calendar?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetMoodData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/mood?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSleepData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/sleep?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetMedicationsData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/medications?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetHistoryData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/history?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetFoodData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/food?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetActivityData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`${API_URL}/activity?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSharingData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`http://localhost:8080/api/sharing?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}

export const GetSettingsData = async () => {
    const token = localStorage.getItem('token');
    try {
        const response = await axios.get(`http://localhost:8080/api/settings?token=${token}`);
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 204) {
            return "Brak danych";
        }
        throw error;
    }
}


