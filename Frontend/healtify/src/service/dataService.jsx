import axios from "axios";

export const GetGeneralData = async () => {
    const token = localStorage.getItem('token')

    let response = await axios.get(`http://localhost:8080/api/data/general?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
        const data = response.data;
    return data;
}

export const GetHeartData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/heart?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}

export const GetSymptomsData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/symptoms?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}

export const GetCalendarData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/calendar?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetMoodData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/mood?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetSleepData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/sleep?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetMedicationsData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/medications?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetHistoryData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/history?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetFoodData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/food?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetActivityData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/data/activity?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}

export const GetSharingData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/sharing?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


export const GetSettingsData = async () => {
    const token = localStorage.getItem('token');

    let response = await axios.get(`http://localhost:8080/api/settings?token=${token}`, { 
        headers: {
            Authorization: "Bearer " + token,
        }});
    return response.data;
}


