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
        const response = await axios.get(`${API_URL}/checkadmin`);
        // console.log(response.data);
        return response.data;
    } catch (error) {
        throw new Error("Failed to check admin status");
    }
};

export const getAllUsers = async () => {
    const response = await axios.get(`${API_URL}/getall`);
    return response.data;
};

const ADMIN_URL = 'http://localhost:8080/api/admin';

/** Lista użytkowników razem z rolami - po to, żeby było widać kto jest lekarzem. */
export const getUsersWithRoles = async () => {
    const response = await axios.get(`${ADMIN_URL}/users`);
    return Array.isArray(response.data) ? response.data : [];
};

/**
 * Nadanie roli lekarza. Backend robi tu dwie rzeczy naraz: dokłada ROLE_DOCTOR
 * i zakłada profil w tabeli doctors - konto z samą rolą nie przeszłoby przez /api/doctor.
 */
export const grantDoctorRole = async (userId, doctorName, specialization) => {
    const response = await axios.post(`${ADMIN_URL}/users/${userId}/grant-doctor`, {
        doctorName,
        specialization,
    });
    return response.data;
};
