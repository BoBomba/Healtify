import axios from "axios";

export const GetGeneralData = async () => {
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    // const config = {
    //     headers: {
    //         Authorization: `Bearer ${token}`
    //     }
    // };

    let response = await axios.get(`http://localhost:8080/api/data/general?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
        const data = response.data;
    return data;
}

export const GetHeartData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/heart", config);
    return response.data;
}

export const GetSymptomsData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/symptoms", config);
    return response.data;
}

export const GetCalendarData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/calendar", config);
    return response.data;
}

export const GetMoodData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/mood", config);
    return response.data;
}

export const GetSleepData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/sleep", config);
    return response.data;
}

export const GetMedicationsData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/medications", config);
    return response.data;
}

export const GetHistoryData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/history", config);
    return response.data;
}

export const GetFoodData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/food", config);
    return response.data;
}

export const GetActivityData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/data/activity", config);
    return response.data;
}

export const GetSharingData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/sharing", config);
    return response.data;
}

export const GetSettingsData = async () => {
    const token = localStorage.getItem('token');
    const config = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    let response = await axios.get("http://localhost:8080/api/settings", config);
    return response.data;
}

