import axios from 'axios';
const API_URL = import.meta.env.VITE_API_URL;

export const postData = async (data) => {
  try {
    const response = await axios.post(API_URL, data);
    return response.data;
  } catch (error) {
    throw new Error(`Failed to save data: ${error.message}`);
  }
};

export const loginPost = async (data) => {
  try {
    const response = await axios.post(`${API_URL}/api/v1/auth/login`, data);
    return response.data;
  } catch (error) {
    throw new Error(`Failed to login: ${error.message}`);
  }
};

export const postRegister = async (data) => {
  try {
    const response = await axios.post(`${API_URL}/api/v1/auth/register`, data);
    return response.data;
  } catch (error) {
    throw new Error(`Failed to save data: ${error.message}`);
  }
};

export const postDeposit = async (amount, accountNumber) => {
  const token = localStorage.getItem('token');
  try {
    const resp = await axios.post(
      `${API_URL}/api/v1/account/transactions/deposit`,
      {
        amount: parseFloat(amount),
        accountNumber: accountNumber,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );
    return resp.data;
  } catch (error) {
    throw new Error(`Error depositing funds: ${error.message}`);
  }
};
