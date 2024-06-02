import {registerService , loginService} from '../service/authService';

export const validateRegisterData = (username,  email, password, confirmPassword) => {

    const data = {
        username: username,
        email: email,
        password: password,
        confirmPassword: confirmPassword
    };

    console.log("Przyjete dane: " , data);

    if (data.email === "" || data.password === "" || data.confirmPassword === "" || data.username === "") {
        alert("Please fill in all fields!");
        return;
    }

    if (!data.email.includes("@")) {
        alert("Invalid email!");
        return;
    }

    if (data.password.length < 6) {
        alert("Password must be at least 6 characters long!");
        return;
    }

    if (data.password !== data.confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    if (data.username.length < 3) {
        alert("Username must be at least 3 characters long!");
        return;
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

    return (
        registerService(data.username, data.email, data.password)
    );
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
