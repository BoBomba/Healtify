import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect , useState} from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import { GetCalendarData } from '../../service/dataService';


function CalendarData() {

  const [data, setData] = useState(null);

  useEffect(() => {
    validateToken();
    GetCalendarData().then(fetchedData => setData(fetchedData));
  });

  return (
    <div>
        
        <Nav />
        data
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Kalendarz</div>
              <div className="datablock"> {data} </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default CalendarData