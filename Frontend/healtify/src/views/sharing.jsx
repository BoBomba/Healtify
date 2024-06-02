import React from 'react'
import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';

function Sharing() {

  useEffect(() => {
    validateToken();
  });

  return (
    <div>
        
        <Nav />
        sharing
        
    </div>
  )
}

export default Sharing