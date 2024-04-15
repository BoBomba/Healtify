import logo from '../images/Healtify.svg';
import '../css/Global.css';
import React from 'react';
import { Link } from 'react-router-dom';

function App() {
  // TODO - dopracować wygląd strony głównej
  return (
    <div className="main-container">
      <img id='logo' src={logo} alt="Logo" style={{ width: '200px', height: 'auto' }} />
      {/* <img src={logo} alt="Icon" /> */}
      {/* <Link
            key={profile}
            to={'/test'}
            to={`/test/${profile}`}
            className={({ isActive }) => {
              return isActive ? 'text-primary-700' : '';
            }}
          >
            Profile Page
      </Link> */}
      {/* <a id="logreg" href="/test">Zaloguj się</a>
      <a id="logreg" href="/">Zarejestruj się</a> */}
      <Link id="logreg" to="/login">Zaloguj się</Link>
      <Link id="logreg" to="/register">Zarejestruj się</Link>
    </div>
   
  );
}

export default App;
