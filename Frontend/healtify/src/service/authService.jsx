import axios from "axios";

export const registerService = async (username, email, password) => {
    const data = {
        username: username,
        email: email,
        password: password,
    };

    await axios.post("http://localhost:8080/register", data)
        .then((response) => {
            if (response.data) {
                alert("User registered successfully Status:" + response.status);
                window.location.href = "/login";
            }
        })
        .catch((error) => {
            console.log(error);
        });
};

export const loginService = async (email, password) => {
    const data = {
        email: email,
        password: password,
    };

    await axios.post("http://localhost:8080/login", data)
        .then((response) => {
            if (response.data) {
                window.location.href = "/";
            } else {
                alert("Invalid password");
            }
        })
        .catch((error) => {
            console.log(error);
        });
};
