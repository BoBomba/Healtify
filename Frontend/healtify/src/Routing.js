import React from 'react';
import { createBrowserRouter} from 'react-router-dom';


import Main from './views/App';
import Test from './views/test';
import Err404 from './views/err404';
import Login from './views/login';
import Register from './views/register';
import Dashboard from './views/dashboard';


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
    path: '/register',
    element: <Register />,
  },
  {
    path: '/dashboard',
    element: <Dashboard />,
  },
  {
    path: '*',
    redirectTo: '/404',
  }
]);

export default router;
