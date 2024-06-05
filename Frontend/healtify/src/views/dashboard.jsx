import React from "react";
// import { Link } from 'react-router-dom';
import "../css/dashboard.css";
import Nav from "../Components/Nav";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { validateToken } from "../service/authService";
import {
  GetGeneralData,
  GetHeartData,
  GetSymptomsData,
} from "../service/dataService";

function Dashboard() {
  const navigate = useNavigate();
  const [generalData, setGeneralData] = useState(null);
  const [heartData, setHeartData] = useState(null);
  const [symptomsData, setSymptomsData] = useState(null);

  //TODO Zmienić na sprawdzanie poprawności tokenu w api

  const renderData = (data, parentKey = '') => {
    return Object.entries(data).map(([key, value]) => {
        const newKey = parentKey ? `${parentKey}.${key}` : key;
        if (value !== null && typeof value === 'object') {
            return renderData(value, newKey);
        } else {
            return <p key={newKey}>{`${newKey}: ${value}`}</p>;
        }
    });
};

  useEffect(() => {
    validateToken();
    GetGeneralData().then((fetchedData) => {
      if (fetchedData === "Unauthorized") {
        navigate("/login");
      } else {
        setGeneralData(fetchedData);
      }
    });
    GetHeartData().then((fetchedData) => {
      if (fetchedData === "Unauthorized") {
        navigate("/login");
      } else {
        setHeartData(fetchedData);
      }
    });
    GetSymptomsData().then((fetchedData) => {
      if (fetchedData === "Unauthorized") {
        navigate("/login");
      } else {
        setSymptomsData(fetchedData);
      }
    });
  }, [navigate]);

  return (
    <div className="dashboard">
      <Nav />

      <main>
        <div className="main-container">
          <div className="datablock">Ogólne Dane</div>
          <div className="datablock">
            {/* {generalData && renderData(generalData)} */}
          </div>
        </div>
        <div className="main-container">
          <div className="datablock">Serce</div>
          <div className="datablock">
            {/* {heartData && renderData(heartData)} */}
            </div>
        </div>
        <div className="main-container">
          <div className="datablock">Symptomy</div>
          <div className="datablock">
          {/* {symptomsData && renderData(symptomsData)} */}
          </div>
        </div>
        {/* TODO Dodać wiecej widoków */}
      </main>

      <footer>Damian Guca</footer>
    </div>
  );
}

export default Dashboard;
