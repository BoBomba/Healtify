import React from 'react'
import '../css/dashboard.css';
import '../css/data.css';
import '../css/calendar.css';
import '../css/doctor.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Nav from '../Components/Nav';
import GrantDoctorModal from '../Components/GrantDoctorModal';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';
import { checkAdminStatus, deleteUserAccount, getUsersWithRoles } from '../service/adminService';

function AdminPanel() {

  const [users, setUsers] = useState([]);
  // Użytkownik, dla którego otwarte jest potwierdzenie nadania roli lekarza.
  const [grantingFor, setGrantingFor] = useState(null);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  async function checkCondition() {
    // 403 z /checkadmin to zwykłe "nie jesteś adminem", ale checkAdminStatus
    // zamienia je na wyjątek, więc trzeba go tu złapać. Bez tego leciał dalej jako 
    // nieobsłużony i przekierowanie w ogóle nie działało.
    let isAdmin = false;
    try {
      isAdmin = await checkAdminStatus();
    } catch (error) {
      console.log(error);
    }
    console.log(isAdmin);

    if (isAdmin === true) {
      console.log("You are an admin");
      return;
    }

    alert("You are not an admin");
    navigate('/dashboard', { replace: true });
  }

  const loadUsers = () => {
    getUsersWithRoles()
      .then((data) => setUsers(data))
      .catch((error) => console.error('There was an error!', error));
  };

  useEffect(() => {
    validateToken();
    // checking if Admin
    checkCondition();
    loadUsers();
  }, []);

  const openGrantModal = (user) => {
    setGrantingFor(user);
    setMessage('');
  };

  // Role są czytane z bazy przy każdym żądaniu, więc działa to od razu bez reloga i bez wymiany tokenu.
  const handleGranted = (user) => {
    setMessage(
      `Rola lekarza nadana kontu ${user.username}. Dane zawodowe uzupełni przy pierwszym zalogowaniu.`
    );
    setGrantingFor(null);
    loadUsers();
  };

  const handleDelete = async (user) => {
    
    const confirmed = window.confirm(
      `Usunąć konto ${user.username} (${user.email})?\n\n` +
      'Znikną razem z nim: wpisy w dzienniku, wizyty (także te umówione jako lekarz), ' +
      'powiązania z lekarzami i pacjentami oraz profil lekarza.\n\n' +
      'Tej operacji nie da się cofnąć.'
    );
    if (!confirmed) return;

    try {
      await deleteUserAccount(user.userId);
      setMessage(`Konto ${user.username} zostało usunięte.`);
      // Potwierdzenie nadania roli mogło być otwarte właśnie dla tego konta.
      if (grantingFor?.userId === user.userId) {
        setGrantingFor(null);
      }
      loadUsers();
    } catch (error) {
      console.log(error);
      setMessage(error.response?.data?.message || 'Nie udało się usunąć konta.');
    }
  };

  return (
    <div className="dashboard">
      <Nav />
        <main>
          <div className="doctor-page">
            <h2>Uzytkownicy</h2>

            {message && <div id="messages">{message}</div>}

            <div className="datablock doctor-panel">
              {users.length === 0 && <p>Brak użytkowników.</p>}

              {users.map(user => {
                const isDoctor = user.roles.includes('ROLE_DOCTOR');
                return (
                  <div className="doctor-list-row" key={user.userId}>
                    <div className="doctor-list-main">
                      <strong>{user.username}</strong>
                      <span className="doctor-list-sub">
                        {user.email} - {user.roles.join(', ')}
                      </span>
                    </div>
                    <div className="doctor-list-actions">
                      {isDoctor ? (
                        <span className="doctor-badge">Lekarz</span>
                      ) : (
                        <button
                          type="button"
                          className="modal-btn primary small"
                          onClick={() => openGrantModal(user)}
                        >
                          Nadaj rolę lekarza
                        </button>
                      )}
                      <button
                        type="button"
                        className="modal-btn danger small"
                        onClick={() => handleDelete(user)}
                      >
                        Usuń konto
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>

          </div>
        </main>

        <GrantDoctorModal
          isOpen={grantingFor !== null}
          onClose={() => setGrantingFor(null)}
          onGranted={handleGranted}
          user={grantingFor}
        />
    </div>
  )
}

export default AdminPanel
