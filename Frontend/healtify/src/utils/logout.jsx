import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { logoutRequest } from '../service/http';

/**
 * Wylogowanie: backend unieważnia tokeny i kasuje ciasteczka (HttpOnly - JS ich nie ruszy),
 * potem odsyłamy na stronę logowania. Potwierdzenie trafia do sessionStorage,
 * a strona logowania pokazuje je w #notice.
 */
const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const logout = async () => {
            try {
                await logoutRequest();
                sessionStorage.setItem('authNotice', 'Zostałeś wylogowany.');
            } catch (error) {
                console.log(error);
                sessionStorage.setItem('authNotice', 'Nie udało się wylogować na serwerze. Spróbuj ponownie.');
            }
            localStorage.removeItem('username');
            // replace - po wylogowaniu "wstecz" nie ma wracać na /logout
            navigate('/login', { replace: true });
        };
        logout();
    }, [navigate]);

    return null;
};

export default Logout;
