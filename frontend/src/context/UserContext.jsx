import { createContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({});

  const token = localStorage.getItem('token');
  const isLoggedIn = !!token;

  const navigate = useNavigate();

  const logoutHandler = () => {
    localStorage.clear();
    sessionStorage.clear();
    // updateUser();
    navigate('/', { replace: true });
  };

  return (
    <UserContext.Provider
      value={{
        token,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

UserContext.displayName = 'UserContext';

export default UserContext;
