import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';


function GeneralData() {

  useEffect(() => {
    validateToken();
  });

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Ogólne Dane</div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default GeneralData