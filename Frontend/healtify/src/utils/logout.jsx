import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * Wylogowanie: czyści sesję i odsyła na stronę logowania.
 * Potwierdzenie trafia do sessionStorage, a strona
 * logowania pokazuje je w #notice.
 */
const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        sessionStorage.setItem('authNotice', 'Zostałeś wylogowany.');
        // replace - po wylogowaniu "wstecz" nie ma wracać na /logout
        navigate('/login', { replace: true });
    }, [navigate]);

    return null;
};

export default Logout;
