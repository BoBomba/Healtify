import React from 'react'
import '../css/dashboard.css';
import axios from 'axios';
import { useState } from 'react';
import Nav from '../Components/Nav';
import { useEffect } from 'react';
import { validateToken } from '../service/authService';
import { checkAdminStatus } from '../service/adminService';

function AdminPanel() {

  const [users, setUsers] = useState([]);

  async function checkCondition() {
    const isAdmin = await checkAdminStatus();
    console.log(isAdmin);
    if (isAdmin === true) {
      console.log("You are an admin");
    } else {
      alert("You are not an admin");
      window.location.href = '/dashboard';
    }
  }

  useEffect(() => {
    validateToken();
    // checking if Admin
    checkCondition();

    // Pobierz listę użytkowników z serwera

    const token = localStorage.getItem('token');
    console.log(token);
    axios.get('http://localhost:8080/api/user/getall', { headers: { Authorization: token } }) 
      .then(response => {
        console.log(response.data);
        setUsers(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });

  }, []); ;

  return (
    <div>
      <Nav />
        <main>
          <div className="block-container">
            <div className="block-row">
              <div className="datablock">Uzytkownicy</div>
              <div className="datablock">
              <table>
                <thead>
                  <tr>
                    <th>Imię</th>
                    <th>Email</th>
                    {/* Dodaj więcej nagłówków zgodnie z danymi użytkownika */}
                  </tr>
                </thead>
                <tbody>
  {users.map(user => (
    <tr key={user.id}>
      <td>{user.username}</td>
      <td>{user.email}</td>
      {/* Dodaj więcej komórek zgodnie z danymi użytkownika */}
    </tr>
  ))}
</tbody>
              </table>
              </div>
            </div>
          </div>
          
        </main> 
    </div>
  )
}

export default AdminPanel