import axios from 'axios';
import { navigateTo } from '../utils/navigation';

const API_URL = 'http://localhost:8080/api/user'; 

const authConfig = (token = localStorage.getItem('token')) => ({
    headers: { Authorization: `Bearer ${token}` }
});

class User {
    constructor(username, email, password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

export const createUser = async (user) => {
    const response = await axios.post(`${API_URL}/add`, User);
    return response.data;
};

export const getUser = async (token) => {
    const response = await axios.get(`${API_URL}/get`, authConfig(token));
    return response.data;
};

/**
 * dane uzytkownika z rolami i gotowymi flagami admin/doctor.
 * Na tej podstawie Nav dobiera linki i decyduje gdzie przekierować.
 */
export const getCurrentUser = async (token) => {
    const response = await axios.get(`${API_URL}/me`, authConfig(token));
    return response.data;
};

export const updateUsername = async (username) => {
    const token = localStorage.getItem('token');
    console.log({username});
    try {
        const response = await axios.patch(`${API_URL}/update-username`, { username }, {
            ...authConfig(token)
        });

        const data = response.data;
        alert("Username updated successfully, you must relogin: " + data);
        navigateTo("/login");
    } catch (error) {
        alert("error: " + error);
    }
}

export const updateEmail = async (email) => {
    const token = localStorage.getItem('token');
    console.log(email);
    try {
        const response = await axios.patch(`${API_URL}/update-email`, {email}, {
            ...authConfig(token)
        });

        const data = response.data;
        alert("Email updated successfully, you must relogin: " + data);
        navigateTo("/login");
    } catch (error) {
        alert("error: " + error);
    }
}

export const updatePassword = async (password, newPassword) => {
    const token = localStorage.getItem('token');
    console.log({password, newPassword});
    try {
        const response = await axios.patch(`${API_URL}/update-password`, { password, newPassword}, {
            ...authConfig(token)
        });

        const data = response.data;
        alert("Password updated successfully, you must relogin: " + data);
        navigateTo("/login");
    } catch (error) {
        alert("error: " + error);
    }
}

/**
 * Skasowanie własnego konta razem ze wszystkimi danymi
 * Nie da się cofnąć!!!
 * Czyszczenie sesji zostaje po stronie widoku.
 *
 * Hasło idzie w ciele żądania i backend sprawdza je u siebie. 
 * W axiosie ciało DELETE przekazuje się przez `data`.
 */
export const deleteUser = async (password, token) => {
    const response = await axios.delete(`${API_URL}/delete`, {
        ...authConfig(token),
        data: { password }
    });
    return response.data;
}
