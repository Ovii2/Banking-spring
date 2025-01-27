import { createContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { logout } from '../services/logout';
import { toast } from 'react-toastify';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({});
  const [token, setToken] = useState(localStorage.getItem('token'));
  const isLoggedIn = !!token;
  const navigate = useNavigate(); 

  const logoutHandler = async () => {
    try {
      await logout();
      localStorage.clear();
      sessionStorage.clear();
      setToken(null);
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
