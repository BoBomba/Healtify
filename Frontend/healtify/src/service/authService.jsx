import axios from "axios";
import { getCurrentUser } from "./userService";
import { navigateTo } from "../utils/navigation";

const API_URL = "http://localhost:8080/api/auth"; // Zmień na adres URL swojego serwera

// Zwraca ścieżkę do panelu użytkownika w zależności od jego roli.

const landingPageForCurrentUser = async () => {
    try {
        const user = await getCurrentUser();

        // Konto, które nie uzupełniło jeszcze swoich danych, trafia najpierw na formularz.
        const needsSetup = user.profileCompleted === false;

        // if else czy doktor czy nie
        if (user.doctor === true) {
            return needsSetup ? "/doctor/profile?setup=1" : "/doctor/dashboard";
        }

        // Admin nie prowadzi dziennika, więc formularz danych pacjenta go nie dotyczy.
        if (user.admin === true) {
            return "/admin/dashboard";
        }

        return needsSetup ? "/data/profile?setup=1" : "/dashboard";
    } catch (error) {
        console.log(error);
        return "/dashboard";
    }
};

/**
 * Handling z Axiosa 
 * Bo np. przy braku połączenia jest samo error.request - czytanie
 * wtedy error.response.status wywalało aplikację zamiast pokazać komunikat.
 */
const describeRequestError = (error) => {

    if (error.response) {
        const details = error.response.data?.message ?? error.response.data;
        const status = `Kod błędu: ${error.response.status}`;
        return typeof details === "string" && details ? `${status}\n${details}` : status;
    }

    if (error.request) {
        return "Brak połączenia z serwerem. Sprawdź, czy backend działa, i spróbuj ponownie.";
    }

    return error.message || "Nieznany błąd.";
};

// Zwraca null przy udanej rejestracji albo komunikat do pokazania w form.

export const registerService = async (username, email, password) => {
    const data = {
        username: username,
        email: email,
        password: password,
    };

    console.log("wysylane dane " + data.username);

    return axios.post(`${API_URL}/register`, data)
        .then((response) => {
            if (response.data) {
                // Potwierdzenie rzuca na ekran logowania + #notice 
                sessionStorage.setItem('authNotice', 'Konto zostało utworzone. Możesz się teraz zalogować.');
                navigateTo("/login");
                return null;
            }

            return "Rejestracja się nie powiodła. Spróbuj ponownie.";
        })
        .catch((error) => {
            console.log(error);

            if (error.response && error.response.status === 409) {
                return "Konto z tym adresem e-mail lub nazwą użytkownika już istnieje.";
            }

            return "Rejestracja się nie powiodła.\n" + describeRequestError(error);
        });
};


// Zwraca null przy poprawnym logowaniu albo komunikat do pokazania w form. (jak w registerService)

export const loginService = async (email, password) => {
    const data = {
        email: email,
        password: password,
    };

    console.log("Wysylane dane: " , data);

    return axios.post(`${API_URL}/authenticate`, data)
        .then(async (response) => {
            if (response.data) {
                console.log("User logged in successfully");
 
                const token = response.data.access_token;
                console.log("Token: ", token);

                localStorage.setItem('token', token);

                // Lekarz ma własny panel (/doctor/*) - o roli decyduje backend.
                await navigateTo(await landingPageForCurrentUser());
                return null;
            }

            return "Nieprawidłowy e-mail lub hasło.";
        })
        .catch((error) => {
            console.log(error);
            return "Logowanie się nie powiodło.\n" + describeRequestError(error);
        });
};


export const validateToken = async () => {
    const token = localStorage.getItem('token');
    console.log("Token: ", token);

    if (token) {
        await axios.post(
            `${API_URL}/validate`,
            {},
            { headers: { Authorization: `Bearer ${token}` } }
        )
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
                navigateTo("/login");
            });
    } else {
        alert("Token is invalid - error");
        localStorage.removeItem('token');
        navigateTo("/login");
    }
}
