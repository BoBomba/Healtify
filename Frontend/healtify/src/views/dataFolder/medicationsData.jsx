import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect , useState} from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import { GetMedicationsData } from '../../service/dataService';


function MedicationsData() {

  const [data, setData] = useState(null);

  useEffect(() => {
    validateToken();
    GetMedicationsData().then(fetchedData => setData(fetchedData));
  });

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Leki</div>
              <div className="datablock"> {data} </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default MedicationsData