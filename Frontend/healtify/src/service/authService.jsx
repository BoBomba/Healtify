import axios from "axios";
// import { useNavigate } from "react-router-dom";

//TODO Zmienić window.location.href na useNavigate

export const registerService = async (username, email, password) => {
    const data = {
        username: username,
        email: email,
        password: password,
    };
    
    console.log("wysylane dane " + data.username);

    await axios.post("http://localhost:8080/api/auth/register", data)
        .then((response) => {
            if (response.data) {
                alert("User registered successfully Status:" + response.status);

                window.location.href = "/login";
            }
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                alert("Conflict: User with this email already exists.");
            } else {
                console.log(error);
            }
        });
};

export const loginService = async (email, password) => {
    const data = {
        email: email,
        password: password,
    };

    console.log("Wysylane dane: " , data);


    await axios.post("http://localhost:8080/api/auth/authenticate", data)
        .then((response) => {
            if (response.data) {
                // console.log(response);
                console.log("User logged in successfully");
                alert("User logged in successfully");

                // Odbieranie tokena z odpowiedzi
                const token = response.data.access_token;
                console.log("Token: ", token);

                // saving token in local storage
                localStorage.setItem('token', token);

                window.location.href = "/dashboard";
            } else {
                alert("Invalid password");
            }
        })
        .catch((error) => {
            console.log(error);
            alert("Logowanie się nie powiodło\nKod Błędu: " + error.response.status + "\n" + error.response.data);
        });
};


export const validateToken = async () => {
    const token = localStorage.getItem('token');
    console.log("Token: ", token);

    if (token) {
        await axios.post(`http://localhost:8080/api/auth/validate?token=${token}`, {
        })
            .then((response) => {
                // Odpowiedź z serwera
                const data = response.data;
                const username = data.username;
                console.log(`Username: ${username}`);
                localStorage.setItem('username', data.username);
                return response.data.username;
            })
            .catch((error) => {
                alert("Token is invalid");
                localStorage.removeItem('token');
                window.location.href = "/login";
            });
    } else {
        alert("Token is invalid - error");
        localStorage.removeItem('token');
        window.location.href = "/login";
    }
}