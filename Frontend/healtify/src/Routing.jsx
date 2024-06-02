import React from 'react';
import { createBrowserRouter } from 'react-router-dom';


import Main from './views/App';
import Test from './views/test';
import Err404 from './views/NotFoundPage';
import Login from './views/login';
import Register from './views/register';
import Dashboard from './views/dashboard';
import Data from './views/data';
import Sharing from './views/sharing';
import Settings from './views/settings';
import Logout from './utils/logout';
import GeneralData from './views/dataFolder/generalData';
import HeartData from './views/dataFolder/heartData';
import SymptomsData from './views/dataFolder/symptomsData';
import CalendarData from './views/dataFolder/calendarData';
import MoodData from './views/dataFolder/moodData';
import SleepData from './views/dataFolder/sleepData';
import MedicationsData from './views/dataFolder/medicationsData';
import HistoryData from './views/dataFolder/historyData';
import FoodData from './views/dataFolder/foodData';
import ActivityData from './views/dataFolder/activityData';


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
    path: '/data/heart',
    element: <HeartData />,
  },
  {
    path: '/data/symptoms',
    element: <SymptomsData />,
  },
  {
    path: '/data/calendar',
    element: <CalendarData />,
  },
  {
    path: '/data/mood',
    element: <MoodData />,
  },
  {
    path: '/data/sleep',
    element: <SleepData />,
  },
  {
    path: '/data/medications',
    element: <MedicationsData />,
  },
  {
    path: '/data/history',
    element: <HistoryData />,
  },
  {
    path: '/data/food',
    element: <FoodData />,
  },
  {
    path: '/data/activity',
    element: <ActivityData />,
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
    path: '*',
    redirectTo: '/404',
  }
]);

export default router;
