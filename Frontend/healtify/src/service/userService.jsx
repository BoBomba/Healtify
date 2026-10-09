import axios from 'axios';
import { navigateTo } from '../utils/navigation';

const API_URL = 'http://localhost:8080/api/user'; 

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

export const getUser = async () => {
    const response = await axios.get(`${API_URL}/get`);
    return response.data;
};

/**
 * dane uzytkownika z rolami i gotowymi flagami admin/doctor.
 * Na tej podstawie Nav dobiera linki i decyduje gdzie przekierować.
 */
export const getCurrentUser = async () => {
    const response = await axios.get(`${API_URL}/me`);
    return response.data;
};

export const updateUsername = async (username) => {
    console.log({username});
    try {
        const response = await axios.patch(`${API_URL}/update-username`, { username });

        const data = response.data;
        alert("Username updated successfully, you must relogin: " + data);
        navigateTo("/login");
    } catch (error) {
        alert("error: " + error);
    }
}

export const updateEmail = async (email) => {
    console.log(email);
    try {
        const response = await axios.patch(`${API_URL}/update-email`, {email});

        const data = response.data;
        alert("Email updated successfully, you must relogin: " + data);
        navigateTo("/login");
    } catch (error) {
        alert("error: " + error);
    }
}

export const updatePassword = async (password, newPassword) => {
    try {
        const response = await axios.patch(`${API_URL}/update-password`, { password, newPassword});

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
export const deleteUser = async (password) => {
    const response = await axios.delete(`${API_URL}/delete`, {
        data: { password }
    });
    return response.data;
}
