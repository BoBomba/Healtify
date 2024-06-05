import React from 'react'
import { Link } from 'react-router-dom';
import '../css/dashboard.css';
import Nav from '../Components/Nav';
import { useEffect , useState} from 'react';
import { validateToken } from '../service/authService';
import { GetSettingsData } from '../service/dataService';
import { RenderData } from "../Components/RenderData";

function Settings() {

  const [data, setData] = useState(null);

  useEffect(() => {
    validateToken();
    GetSettingsData().then(fetchedData => setData(fetchedData));
  });


  return (

    <div>

        <Nav />
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Ustawienia</div>
              <div className="datablock"> {data} </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default Settings