import { useEffect } from 'react';

/**
 * Wylogowanie: czyści sesję i odsyła na stronę logowania.
 *
 * Potwierdzenie trafia do sessionStorage, a strona
 * logowania pokazuje je jako komunikat w #notice.
 */
const Logout = () => {
    useEffect(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        sessionStorage.setItem('authNotice', 'Zostałeś wylogowany.');
        window.location.href = '/login';
    }, []);

    return null;
};

export default Logout;
