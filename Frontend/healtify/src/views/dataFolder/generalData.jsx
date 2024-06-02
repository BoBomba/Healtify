import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect, useState } from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import { GetGeneralData } from '../../service/dataService';


function GeneralData() {

    const [data, setData] = useState(null);

    useEffect(() => {
      validateToken();
      GetGeneralData().then(fetchedData => setData(fetchedData));
    }, []);

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Ogólne Dane</div>
              <div className="datablock">generalData: {data} </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default GeneralData