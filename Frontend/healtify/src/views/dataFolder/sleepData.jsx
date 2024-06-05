import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect , useState } from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import { GetSleepData } from '../../service/dataService';


function SleepData() {

  const [data, setData] = useState(null);

  useEffect(() => {
    validateToken();
    GetSleepData().then(fetchedData => setData(fetchedData));
  });

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Sen</div>
              <div className="datablock"> {data} </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default SleepData