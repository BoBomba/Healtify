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
import { RenderData } from "../Components/RenderData";

function Dashboard() {
  const [generalData, setGeneralData] = useState(null);
  const [heartData, setHeartData] = useState(null);
  const [symptomsData, setSymptomsData] = useState(null);

  useEffect(() => {
    validateToken();
    GetGeneralData().then((fetchedData) => {
      if (fetchedData === "null") {
        setGeneralData(null);
      } else {
        setGeneralData(fetchedData);
      }
    });
    GetHeartData().then((fetchedData) => {
      if (fetchedData === "null") {
        setHeartData(null);
      } else {
        setHeartData(fetchedData);
      }
    });
    GetSymptomsData().then((fetchedData) => {
      if (fetchedData === "null") {
        setSymptomsData(null);
      } else {
        setSymptomsData(fetchedData);
      }
    });
  }, []);

  return (
    <div className="dashboard">
      <Nav />

      <main>
        <div className="main-container">
          <div className="datablock">Ogólne Dane</div>
          <div className="datablock">
            {generalData && RenderData(generalData)}
          </div>
        </div>
        <div className="main-container">
          <div className="datablock">Serce</div>
          <div className="datablock">
            {heartData && RenderData(heartData)}
            </div>
        </div>
        <div className="main-container">
          <div className="datablock">Symptomy</div>
          <div className="datablock">
          {symptomsData && RenderData(symptomsData)}
          </div>
        </div>
        {/* TODO Dodać wiecej widoków */}
      </main>

      <footer>Damian Guca</footer>
    </div>
  );
}

export default Dashboard;
