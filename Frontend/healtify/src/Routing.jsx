import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';


import Main from './views/App';
import Test from './views/test';
import Err404 from './views/NotFoundPage';
import Login from './views/login';
import Register from './views/register';
import ForgotPassword from './views/ForgotPassword';
import Dashboard from './views/dashboard';
import Data from './views/data';
import Sharing from './views/sharing';
import Settings from './views/settings';
import Logout from './utils/logout';
import GeneralData from './views/dataFolder/generalData';
import JournalData from './views/dataFolder/journalData';
import PatientProfile from './views/dataFolder/patientProfile';
import AdminPanel from './views/AdminPanel';
import AdminDashboard from './views/AdminDashboard';
import CalendarPage from './views/Calendar';
import DoctorDashboard from './views/doctor/DoctorDashboard';
import DoctorCalendar from './views/doctor/DoctorCalendar';
import DoctorData from './views/doctor/DoctorData';
import DoctorSharing from './views/doctor/DoctorSharing';
import DoctorProfile from './views/doctor/DoctorProfile';
import DoctorPatientJournal from './views/doctor/DoctorPatientJournal';
import ChatPage from './views/ChatPage';


import './index.css';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Main />,
    errorElement: <Err404 />,
  },
  {
    path: '/test',
    element: <Test />,
    // children: [
    //   {
    //     path: '/profiles/:profileId',
    //     element: <ProfilePage />,
    //   },
    // ],
  },
  {
    path: '/404',
    element: <Err404 />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/forgotpasswd',
    element: <ForgotPassword />,
  },
  {
    path: '/logout',
    element: <Logout />,
  },
  {
    path: '/register',
    element: <Register />,
  },
  {
    path: '/dashboard',
    element: <Dashboard />,
  },
  {
    path: '/data',
    element: <Data />,
  },
  // ?setup=1 to wariant po pierwszym zalogowaniu -
  // strona otwiera sie od razu w trybie edycji i konczy przyciskiem "Gotowe".
  {
    path: '/data/profile',
    element: <PatientProfile />,
  },
  {
    path: '/data/general',
    element: <GeneralData />,
  },
  {
    path: '/data/journal',
    element: <JournalData />,
  },
  {
    path: '/calendar',
    element: <CalendarPage />,
  },
  {
    path: '/sharing',
    element: <Sharing />,
  },
  // Jedna trasa dla obu rol. Przycisk powrotu wraca do wlasciwego panelu.
  {
    path: '/sharing/chat/:sharingId',
    element: <ChatPage />,
  },
  {
    path: '/settings',
    element: <Settings />,
  },
  {
    path: '/admin',
    element: <AdminPanel />,
  },
  // Dostepu pilnuje backend (ROLE_ADMIN).
  {
    path: '/admin/dashboard',
    element: <AdminDashboard />,
  },
  // Dostępu pilnuje backend (ROLE_DOCTOR), front tylko nie pokazuje linków.
  {
    path: '/doctor',
    element: <Navigate to="/doctor/dashboard" replace />,
  },
  {
    path: '/doctor/dashboard',
    element: <DoctorDashboard />,
  },
  {
    path: '/doctor/calendar',
    element: <DoctorCalendar />,
  },
  {
    path: '/doctor/data',
    element: <DoctorData />,
  },
  {
    path: '/doctor/sharing',
    element: <DoctorSharing />,
  },
  // ?setup=1 to pierwsze logowanie po nadaniu roli.
  {
    path: '/doctor/profile',
    element: <DoctorProfile />,
  },
  // Wpisy z dziennika udostepnione lekarzowi.
  {
    path: '/doctor/patients/:patientId/journal',
    element: <DoctorPatientJournal />,
  },
  {
    path: '*',
    element: <Navigate to="/404" replace />,
  }
]);

export default router;
