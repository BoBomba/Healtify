import axios from "axios";
import { navigateTo } from "../utils/navigation";

const AUTH_URL = "http://localhost:8080/api/auth";

// Strony, na których brak sesji to norma - nie przerzucamy z nich na /login.
const PUBLIC_PATHS = ["/", "/login", "/register", "/forgotpasswd", "/logout", "/404"];

/**
 * Wspólna konfiguracja axiosa dla całej aplikacji.
 * Tokeny siedzą w ciasteczkach HttpOnly - JS ich nie widzi, przeglądarka dołącza je sama.
 */
axios.defaults.withCredentials = true;
// Nagłówek X-XSRF-TOKEN także przy żądaniach na inny port (backend :8080).
axios.defaults.withXSRFToken = true;

const hasCsrfCookie = () =>
    document.cookie.split("; ").some((cookie) => cookie.startsWith("XSRF-TOKEN="));

// Pierwszy POST/PUT/... w sesji przeglądarki - najpierw pobieramy token CSRF.
axios.interceptors.request.use(async (config) => {
    const method = (config.method || "get").toLowerCase();
    if (!["get", "head", "options"].includes(method) && !hasCsrfCookie()) {
        await axios.get(`${AUTH_URL}/csrf`);
    }
    return config;
});

let refreshing = null;

// Jedno odświeżenie naraz - równoległe 401 czekają na ten sam wynik,
// bo drugi refresh starym (już zrotowanym) tokenem by nie przeszedł.
export const refreshSession = () => {
    if (!refreshing) {
        refreshing = axios.post(`${AUTH_URL}/refresh-token`)
            .finally(() => { refreshing = null; });
    }
    return refreshing;
};

const sessionExpired = () => {
    if (PUBLIC_PATHS.includes(window.location.pathname)) return;
    sessionStorage.setItem("authNotice", "Sesja wygasła. Zaloguj się ponownie.");
    navigateTo("/login");
};

// Access token wygasł -> odświeżamy sesję i powtarzamy żądanie raz.
// Tylko 401 z markerem z SecurityConfig - "złe hasło" też bywa 401, ale bez niego.
axios.interceptors.response.use(
    (response) => response,
    async (error) => {
        const { config, response } = error;
        if (response?.status !== 401 || response.data?.error !== "unauthenticated" || !config || config._retried) {
            throw error;
        }

        config._retried = true;
        try {
            await refreshSession();
        } catch {
            sessionExpired();
            throw error;
        }
        return axios(config);
    }
);

export const logoutRequest = () => axios.post(`${AUTH_URL}/logout`);
