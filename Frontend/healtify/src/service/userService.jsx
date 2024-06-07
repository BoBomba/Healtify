import axios from 'axios';

const API_URL = 'http://localhost:8080/api/user'; // Zmień na adres URL swojego serwera

class User {
    constructor(username, email, password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

export const createUser = async (user) => {
    const token = localStorage.getItem('token');
    const response = await axios.post(`${API_URL}/add`, User);
    return response.data;
};

export const getUser = async (token) => {
    const response = await axios.get(`${API_URL}/get`, { headers: { Authorization: token } });
    return response.data;
};

export const updateUsername = async (username) => {
    const token = localStorage.getItem('token');
    console.log({username});
    try {
        const response = await axios.patch(`${API_URL}/update-username`, { username }, {
            headers: {
                'Authorization': token
            }
        });
        // Odpowiedź z serwera
        const data = response.data;
        alert("Username updated successfully, you must relogin: " + data);
        window.location.href = "/login";
    } catch (error) {
        alert("error: " + error);
    }
}

export const updateEmail = async (email) => {
    const token = localStorage.getItem('token');
    console.log(email);
    try {
        const response = await axios.patch(`${API_URL}/update-email`, {email}, {
            headers: {
                'Authorization': token
            }
        });
        // Odpowiedź z serwera
        const data = response.data;
        alert("Email updated successfully, you must relogin: " + data);
        window.location.href = "/login";
    } catch (error) {
        alert("error: " + error);
    }
}

export const updatePassword = async (password, newPassword) => {
    const token = localStorage.getItem('token');
    console.log({password, newPassword});
    try {
        const response = await axios.patch(`${API_URL}/update-password`, { password, newPassword}, {
            headers: {
                'Authorization': token
            }
        });
        // Odpowiedź z serwera
        const data = response.data;
        alert("Password updated successfully, you must relogin: " + data);
        window.location.href = "/login";
    } catch (error) {
        alert("error: " + error);
    }
}

export const deleteUser = async (token) => {
    const response = await axios.delete(`${API_URL}/delete`, {
        headers: {
            'Authorization': token
        }
    });
    return response.data;
}