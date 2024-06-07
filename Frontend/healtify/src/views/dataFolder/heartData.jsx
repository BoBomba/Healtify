import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect , useState } from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import { GetHeartData } from '../../service/dataService';
import { RenderData } from "../../Components/RenderData";


function HeartData() {

  const [data, setData] = useState(null);

  useEffect(() => {
    validateToken();
    GetHeartData().then((fetchedData) => {
      if (fetchedData === "null") {
        setData(null);
      } else {
        setData(fetchedData);
      }
    });
  }, []);

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Serce</div>
              <div className="datablock"> {data && RenderData(data)}</div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default HeartData