import axios from "axios";

const API_URL = 'http://localhost:8080/api/user'

axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export const checkAdminStatus = async () => {
    try {
        const response = await axios.get(`${API_URL}/checkadmin`, { headers: { Authorization: localStorage.getItem("token") } });
        console.log(response.data);
        return response.data;
    } catch (error) {
        throw new Error("Failed to check admin status");
    }
};

export const getAllUsers = async () => {
    const response = await axios.get(`${API_URL}/getall`);
    return response.data;
};