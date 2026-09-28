import React from 'react'
import { Link } from 'react-router-dom';
import '../../css/dashboard.css';
import Nav from '../../Components/Nav';
import { useEffect, useState } from 'react';
import { validateToken } from '../../service/authService';
import '../../css/data.css';
import '../../css/profile.css';
import { GetPatientProfile } from '../../service/dataService';
import PatientDetails from '../../Components/PatientDetails';


function GeneralData() {

    const [profile, setProfile] = useState(null);

    useEffect(() => {
      validateToken();
      GetPatientProfile()
        .then((fetchedProfile) => setProfile(fetchedProfile))
        .catch((error) => console.log(error));
    }, []);

  return (
    <div>

        <Nav />
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock profile-panel">
                <h2 className="profile-title">Ogólne Dane</h2>
                <PatientDetails profile={profile} />
                <Link to="/data/profile?edit=1" className="big-btn">Edytuj</Link>
                <Link to="/data" className="big-btn secondary">Powrót</Link>
              </div>
            </div>
          </div>

        </main>
    </div>
  )
}

export default GeneralData
