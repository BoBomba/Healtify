import React from "react";
import "../css/dashboard.css";
import Nav from "../Components/Nav";
import { useEffect, useState } from "react";
import { validateToken } from "../service/authService";
import {
  GetGeneralData,
  GetSymptomsData,
} from "../service/dataService";
import { RenderData } from "../Components/RenderData";

function Dashboard() {
  const [generalData, setGeneralData] = useState(null);
  const [symptomsData, setSymptomsData] = useState(null);

  useEffect(() => {
    validateToken();
    GetGeneralData().then((fetchedData) => {
      console.log(fetchedData);
      if (fetchedData === "null") {
        setGeneralData(null);
      } else {
        setGeneralData(fetchedData);
      }
    });
    GetSymptomsData().then((fetchedData) => {
      console.log(fetchedData);
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
          <div className="datablock">Symptomy</div>
          <div className="datablock">
          {symptomsData && RenderData(symptomsData)}
          </div>
        </div>
      </main>

      <footer>Damian Guca</footer>
    </div>
  );
}

export default Dashboard;
