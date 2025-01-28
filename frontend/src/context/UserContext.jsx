import { createContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { logout } from '../services/logout';
import { toast } from 'react-toastify';
import { jwtDecode } from 'jwt-decode';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({});
  const [token, setToken] = useState(localStorage.getItem('token'));
  const isLoggedIn = !!token;
  const navigate = useNavigate();  

  useEffect(() => {
    if (token) {
      try {
        const { exp } = jwtDecode(token);
        const currentTime = Date.now() / 1000;

        if (exp < currentTime) {
          toast.error('Your session has expired. Please log in again.');
          logoutHandler();
        } else {
          const timeout = setTimeout(logoutHandler, (exp - currentTime) * 1000);
          return () => clearTimeout(timeout);
        }
      } catch (error) {
        console.error('Invalid token:', error);
        logoutHandler();
      }
    }
  }, [token]);

  const logoutHandler = async () => {
    try {
      await logout();
      localStorage.clear();
      sessionStorage.clear();
      setToken(null);
      setUser({});
      toast.success('Logged out successfully');
      navigate('/', { replace: true });
    } catch (error) {
      toast.error('Logout failed');
    }
  };

  return (
    <UserContext.Provider
      value={{
        token,
        setToken,
        isLoggedIn,
        logoutHandler,
        user,
        setUser,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

UserContext.displayName = 'UserContext';

export default UserContext;
