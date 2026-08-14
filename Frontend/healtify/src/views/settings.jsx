import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { validateToken } from '../service/authService';
import { GetUserData } from '../service/dataService';
import * as UserService from '../service/userService';
import Nav from '../Components/Nav';
import DeleteAccountModal from '../Components/DeleteAccountModal';
import Avatar from '../images/Avatar.png';
import User from '../images/user.svg';
import Lock from '../images/lock.svg';
import '../css/settings.css';


function Settings() {
  const [data, setData] = useState(null);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [Password, setPassword] = useState("");
  const [NewPassword, setNewPassword] = useState('');
  const [deleteOpen, setDeleteOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    validateToken();
    GetUserData().then(fetchedData => {
      console.log(fetchedData);
      if (fetchedData && fetchedData.username && fetchedData.email) {
        setData(fetchedData);
        setUsername(fetchedData.username);
        setEmail(fetchedData.email);
      } else {
        setData(null);
        setUsername("");
        setEmail("");
      }
    });
  }, []);

  const handleSubmitusername = async (e) => {
    e.preventDefault();
    setUsername( username );
    console.log(username);
    await UserService.updateUsername(username);
  };

  const handleSubmitemail = async (e) => {
    e.preventDefault();
    setEmail(email);

    await UserService.updateEmail(email);
  };

  const handleSubmitpassword = async (e) => {
    e.preventDefault();
    setPassword(Password);
    console.log(Password);
    console.log(NewPassword);
    await UserService.updatePassword(Password, NewPassword);
  }

  /**
   * Potwierdzenie zostawiamy w sessionStorage tak jak wylogowanie -
   * strona logowania pokaże je po wejściu.
   */
  const handleDeleted = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    sessionStorage.setItem('authNotice', 'Konto zostało usunięte razem ze wszystkimi danymi.');
    navigate('/login', { replace: true });
  }

  return (
    <div>
      <Nav />
      <main>
        <div className="block-container">
          <div className="block-row">
            <div className="datablock"><a1><b>Ustawienia</b></a1></div>
            <div className="datablock">
              <img src={Avatar} alt="Avatar" />
              <p>Username: {username}</p>
              <p>Email: {email}</p>
              <form onSubmit={handleSubmitusername}>
              <div id="input">
                        <img src={User} alt="user" />
                        <input type="text" id="firstname" name="name" placeholder="Wprowadź Nazwe Uzytkownika" value={username} onChange={e => setUsername(e.target.value)}/>
                    </div>
                    <input className="submit" type="submit" value="Update" />
              </form>
              <form onSubmit={handleSubmitemail}>
                    <div id="input">
                        <img src={User} alt="user" />
                        <input type="email" name="email" placeholder="Wprowadź email" value={email} onChange={e => setEmail(e.target.value)}/>
                    </div>
                    <input className="submit" type="submit" value="Update" />
              </form>

              <form onSubmit={handleSubmitpassword}>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź hasło" value={Password} onChange={e => setPassword(e.target.value)}/>
                    </div>
                    <div id="input">
                        <img src={Lock} id="lock" alt="lock" />
                        <input type="password" name="password" placeholder="Wprowadź nowe hasło" value={NewPassword} onChange={e => setNewPassword(e.target.value)}/>
                    </div>
                    <input className="submit" type="submit" value="Update" />
              </form>
            </div>
          </div>

          <div className="block-row">
            <div className="datablock danger-zone">
              <h3>Usuwanie konta</h3>
              <p>
                Usunięcie konta kasuje wszystkie Twoje dane: wpisy w dzienniku, umówione
                wizyty (terminy u lekarzy się zwolnią), powiązania z lekarzami i dane profilu.
                Tej operacji nie da się cofnąć.
              </p>
               {/* Hasło pytamy dopiero w modalu */}
              <button
                type="button"
                className="danger-btn"
                onClick={() => setDeleteOpen(true)}
                disabled={username === ''}
              >
                Usuń konto
              </button>
            </div>
          </div>
        </div>
      </main>

      <DeleteAccountModal
        isOpen={deleteOpen}
        onClose={() => setDeleteOpen(false)}
        onDeleted={handleDeleted}
        username={username}
      />
    </div>
  );
}

export default Settings;
