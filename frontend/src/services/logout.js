import axios from 'axios';
const API_URL = import.meta.env.VITE_API_URL;

const token = localStorage.getItem('token');

export const logout = async () => {
  try {
    await axios.post(`${API_URL}/api/v1/auth/logout`, null, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    localStorage.removeItem('token');
  } catch (error) {
    console.error('Logout error:', error);
  }
};
