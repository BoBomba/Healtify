import logo from '../images/Healtify.svg';
import '../css/Global.css';
import React from 'react';
import { Link } from 'react-router-dom';

function App() {
  // TODO - dopracować wygląd strony głównej
  return (
    <div className="main-container">
      <img id='logo' src={logo} alt="Logo" style={{ width: '200px', height: 'auto' }} />
      <Link id="logreg" to="/login">Zaloguj się</Link>
      <Link id="logreg" to="/register">Zarejestruj się</Link>
    </div>
    
  );
}

export default App;
