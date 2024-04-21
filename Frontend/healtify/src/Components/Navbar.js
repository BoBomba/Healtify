// navbar.js
import { useEffect } from 'react';

export function toggleMenu() {
    var navbar = document.getElementById("myNavbar");
    navbar.style.transform = navbar.style.transform === "translateX(0)" ? "translateX(-101%)" : "translateX(0)";
}

export function useOutsideClick() {
    useEffect(() => {
        function handleClickOutside(event) {
            var navbar = document.getElementById("myNavbar");
            var button = document.querySelector('#navMenu');
            var settings = document.querySelector('#settings');
        
            if (event.target !== navbar && event.target !== button && event.target !== settings) {
                navbar.style.transform = "translateX(-101%)";
            }
        }

        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);
}