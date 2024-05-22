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

                // Możesz teraz zapisać token w localStorage lub w innym bezpiecznym miejscu
                // localStorage.setItem('token', token);

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
