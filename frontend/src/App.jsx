import { useState } from 'react';
import './index.css';
import { Route, Routes } from 'react-router-dom';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import NotFoundPage from './pages/NotFoundPage';

import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './pages/HomePage';
import { ToastContainer } from 'react-toastify';
import AccountPage from './pages/AccountPage';

function App() {
  const [count, setCount] = useState(0);

  return (
    <>
      <ToastContainer autoClose={1200} position='top-center' />
      <Header />
      <Routes>
        <Route path='/' element={<HomePage />} />
        <Route path='/register' element={<RegisterPage />} />
        <Route path='/login' element={<LoginPage />} />
        <Route path='/account' element={<AccountPage />} />
        <Route path='*' element={<NotFoundPage />} />
      </Routes>
      <Footer />
    </>
  );
}

export default App;
