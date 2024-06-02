import React from 'react';

const Logout = () => {
    const handleLogout = () => {
        // remove token
        localStorage.removeItem('token');

        alert("Zostałeś wylogowany");

        window.location.href = '/login';
    };

    return (
        handleLogout()
        );
};

export default Logout;