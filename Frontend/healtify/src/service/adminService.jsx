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

/** Wszyscy lekarze z danymi zawodowymi. */
export const getAllDoctors = async () => {
    const response = await axios.get(`${ADMIN_URL}/doctors`);
    return Array.isArray(response.data) ? response.data : [];
};

/** Liczniki na dashboard admina: pacjenci, lekarze, powiązania, wizyty, wpisy. */
export const getAdminStats = async () => {
    const response = await axios.get(`${ADMIN_URL}/stats`);
    return response.data;
};

/**
 * Nadanie roli lekarza. Backend robi tu dwie rzeczy:
 * dokłada ROLE_DOCTOR i profil w tabeli doctors. 
 */
export const grantDoctorRole = async (userId) => {
    const response = await axios.post(`${ADMIN_URL}/users/${userId}/grant-doctor`, {});
    return response.data;
};

/**
 * Odebranie roli lekarza. Konto zostaje jako pacjent, znika profil lekarza
 * razem z jego wizytami, powiązaniami i czatem.
 */
export const revokeDoctorRole = async (userId) => {
    await axios.delete(`${ADMIN_URL}/users/${userId}/doctor`);
};

/**
 * Skasowanie konta użytkownika razem z jego danymi.
 * Operacji nie da się cofnąć. Własnego konta admin nie skasuje.
 */
export const deleteUserAccount = async (userId) => {
    await axios.delete(`${ADMIN_URL}/users/${userId}`);
};
