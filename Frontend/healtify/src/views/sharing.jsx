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
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Udostepnianie</div>
              <div className="datablock"></div>
            </div>
          </div>
        </main> 
    </div>
  );
}

export default Sharing
