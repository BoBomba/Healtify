import React from 'react'
import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';
import '../css/data.css';


function Data() {

  useEffect(() => {
    validateToken();
  }, []);

  return (
    <div>
      <Nav />
      <main>
        <div className="block-container">
          <div className="block-row">
            <Link to="/data/general" className="datablock">Ogólne Dane</Link>
            <Link to="/data/journal" className="datablock">Dziennik</Link>
          </div>
        </div>
      </main>
    </div>
  )
}

export default Data
