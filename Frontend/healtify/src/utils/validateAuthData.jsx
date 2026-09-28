import {registerService , loginService} from '../service/authService';

/**
 * Zwraca null, gdy rejestracja się udała, albo komunikaty do pokazania
 * w form - najpierw błędy z frontu, potem z backendu.
 */
export const validateRegisterData = async (username,  email, password, confirmPassword) => {

    const data = {
        username: username,
        email: email,
        password: password,
        confirmPassword: confirmPassword
    };

    console.log("Przyjete dane: " , data);

    const errors = [];

    if (!data.username.trim() || !data.email.trim() || !data.password || !data.confirmPassword) {
        errors.push("Wypełnij wszystkie pola.");
    } else {
        if (!data.email.includes("@")) {
            errors.push("Wprowadź poprawny adres e-mail.");
        }

        if (data.username.trim().length < 3) {
            errors.push("Nazwa użytkownika musi mieć co najmniej 3 znaki.");
        }

        if (data.password.length < 6) {
            errors.push("Hasło musi mieć co najmniej 6 znaków.");
        }

        if (data.password !== data.confirmPassword) {
            errors.push("Hasła nie są takie same.");
        }
    }

    if (errors.length > 0) {
        return errors;
    }


    // // Password security check
    // const hasUpperCase = /[A-Z]/.test(data.password);x
    // const hasLowerCase = /[a-z]/.test(data.password);
    // const hasNumber = /[0-9]/.test(data.password);
    // const hasSpecialChar = /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]+/.test(data.password);

    // if (!hasUpperCase || !hasLowerCase || !hasNumber || !hasSpecialChar) {
    //   alert("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character!");
    //   return;
    // }

    console.log("Wysyłanie danych do pliku wysyłającego...");

    const failure = await registerService(data.username, data.email, data.password);

    return failure ? [failure] : null;
}

export const validateLoginData = (email, password) => {
    const data = {
        email: email,
        password: password,
    };

    console.log("Przyjete dane: " , data);

    if (data.email === "" || data.password === "") {
        alert("Please fill in all fields!");
        return;
    }

    if (!data.email.includes("@")) {
        alert("Invalid email!");
        return;
    }
    console.log("Wysyłanie danych do pliku wysyłającego...");

    return loginService(data.email, data.password);
};
