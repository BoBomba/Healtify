import React from 'react';
// import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';
import Message from '../Components/Message';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function Dashboard() {

    const navigate = useNavigate();

    //TODO Zmienić na sprawdzanie poprawności tokenu w api

    useEffect(() => {
        const token = localStorage.getItem('token');

        if (!token) {
            alert('You are not logged in');
            navigate('/login');
        }
    }, [navigate]);

    return (
        <div className="dashboard">
            <Nav />

            <main>
                <div className="main-container">
                    <Message />
                </div>
                <div className="main-container">2.</div>
                <div className="main-container">3.</div>
                {/* TODO Dodać wiecej widoków */}
            </main>

            <footer>
                Damian Guca
            </footer>
        </div>
    );
}


export default Dashboard;
