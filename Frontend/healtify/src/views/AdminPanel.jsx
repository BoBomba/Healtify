import React from 'react'
import '../css/dashboard.css';
import '../css/data.css';
import '../css/calendar.css';
import '../css/doctor.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Nav from '../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';
import { checkAdminStatus, deleteUserAccount, getUsersWithRoles, grantDoctorRole } from '../service/adminService';

function AdminPanel() {

  const [users, setUsers] = useState([]);
  // Formularz nadania roli lekarza rozwija się przy konkretnym użytkowniku.
  const [grantingFor, setGrantingFor] = useState(null);
  const [doctorName, setDoctorName] = useState('');
  const [specialization, setSpecialization] = useState('');
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

  const openGrantForm = (user) => {
    setGrantingFor(user.userId);
    setDoctorName(user.username);
    setSpecialization('');
    setMessage('');
  };

  const handleGrant = async (event) => {
    event.preventDefault();

    if (doctorName.trim() === '') {
      setMessage('Podaj imię i nazwisko lekarza.');
      return;
    }

    try {
      await grantDoctorRole(grantingFor, doctorName.trim(), specialization.trim());
      // Role są czytane z bazy przy każdym żądaniu, więc działa to od razu bez reloga i bez wymiany tokenu.
      setMessage('Rola lekarza nadana. Panel lekarza jest dostępny od razu.');
      setGrantingFor(null);
      loadUsers();
    } catch (error) {
      console.log(error);
      setMessage(error.response?.data?.message || 'Nie udało się nadać roli lekarza.');
    }
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
      // Formularz nadania roli mógł być otwarty właśnie dla tego konta.
      if (grantingFor === user.userId) {
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
                          onClick={() => openGrantForm(user)}
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

            {grantingFor !== null && (
              <div className="datablock doctor-panel">
                <h3>Nowy profil lekarza</h3>
                <form className="modal-form" onSubmit={handleGrant}>
                  <label className="field-label" htmlFor="doctor-name">Imię i nazwisko</label>
                  <input
                    id="doctor-name"
                    type="text"
                    maxLength={120}
                    value={doctorName}
                    onChange={(e) => setDoctorName(e.target.value)}
                  />

                  <label className="field-label" htmlFor="doctor-specialization">Specjalizacja</label>
                  <input
                    id="doctor-specialization"
                    type="text"
                    maxLength={120}
                    placeholder="Np. psychoterapeuta"
                    value={specialization}
                    onChange={(e) => setSpecialization(e.target.value)}
                  />

                  <div className="modal-actions">
                    <button
                      type="button"
                      className="modal-btn secondary"
                      onClick={() => setGrantingFor(null)}
                    >
                      Anuluj
                    </button>
                    <button type="submit" className="modal-btn primary">Nadaj rolę</button>
                  </div>
                </form>
              </div>
            )}
          </div>
        </main>
    </div>
  )
}

export default AdminPanel
