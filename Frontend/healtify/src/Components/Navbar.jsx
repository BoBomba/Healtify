// navbar.js
import { useEffect } from 'react';

// Stan menu trzyma .open, a nie transform - dzięki temu CSS może
// menu schować (przesunięcie zostawiało jego cień na stronie).
export function toggleMenu() {
    document.getElementById("myNavbar")?.classList.toggle("open");
}

export function useOutsideClick() {
    useEffect(() => {
        function handleClickOutside(event) {
            var navbar = document.getElementById("myNavbar");
            var button = document.querySelector('#navMenu');
            var settings = document.querySelector('#settings');
        
            if (event.target !== navbar && event.target !== button && event.target !== settings) {
                navbar?.classList.remove("open");
            }
        }

        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);
}