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
  });

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">a</div>
              <div className="datablock">b</div>
              <div className="datablock">c</div>
              <div className="datablock">d</div>
              <div className="datablock">e</div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default Data