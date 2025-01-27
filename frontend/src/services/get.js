import axios from 'axios';
const API_URL = import.meta.env.VITE_API_URL;

const token = localStorage.getItem('token');

export const getAccountDataByUserId = async (userId) => {
  try {
    const resp = await axios.get(`${API_URL}/api/v1/account/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return resp.data;
  } catch (error) {
    throw new Error(`Error fetching user data: ${error.message}`);
  }
};

export const getUserTransactions = async (userId) => {
  try {
    const resp = await axios.get(`${API_URL}/api/v1/account/transactions/user/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return resp.data;
  } catch (error) {
    throw new Error(`Error fetching transactions: ${error.message}`);
  }
};
