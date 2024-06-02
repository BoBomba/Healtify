import React from 'react'
import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';

function Settings() {

  useEffect(() => {
    validateToken();
  });


  return (

    <div>

        <Nav />
        settings
        
    </div>
  )
}

export default Settings