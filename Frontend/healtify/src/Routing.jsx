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
import AdminPanel from './views/AdminPanel';
import CalendarPage from './views/Calendar';
import DoctorDashboard from './views/doctor/DoctorDashboard';
import DoctorCalendar from './views/doctor/DoctorCalendar';
import DoctorData from './views/doctor/DoctorData';
import DoctorSharing from './views/doctor/DoctorSharing';


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
  {
    path: '/settings',
    element: <Settings />,
  },
  {
    path: '/admin',
    element: <AdminPanel />,
  },
  // Panel lekarza. Osobne ścieżki zamiast wariantów widoków pacjenta - lekarz nie ma
  // dziennika ani danych ogólnych, więc te strony nie mają ze sobą prawie nic wspólnego.
  // Dostępu pilnuje backend (rola ROLE_DOCTOR), front tylko nie pokazuje linków.
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
  {
    path: '*',
    element: <Navigate to="/404" replace />,
  }
]);

export default router;
