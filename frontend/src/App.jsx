import { useContext, useState } from 'react';
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
import UserContext from './context/UserContext';

function App() {
  const { isLoggedIn } = useContext(UserContext);

  return (
    <div className='flex flex-col min-h-screen'>
      <ToastContainer autoClose={1200} position='top-center' />
      <Header />
      <main className='flex-grow'>
        <Routes>
          {!isLoggedIn ? (
            <>
              <Route path='/' element={<HomePage />} />
              <Route path='/register' element={<RegisterPage />} />
              <Route path='/login' element={<LoginPage />} />
            </>
          ) : (
            <Route path='/account' element={<AccountPage />} />
          )}
          <Route path='*' element={<NotFoundPage />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
