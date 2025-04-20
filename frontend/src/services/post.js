import axios from 'axios';
const API_URL = import.meta.env.VITE_API_URL;

const token = localStorage.getItem('token');

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

export const postDeposit = async ({ amount, senderAccountNumber }) => {
  try {
    const resp = await axios.post(
      `${API_URL}/api/v1/account/transactions/deposit`,
      {
        amount: parseFloat(amount),
        senderAccountNumber,
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

export const postWithdraw = async ({ amount, senderAccountNumber }) => {
  try {
    const resp = await axios.post(
      `${API_URL}/api/v1/account/transactions/withdraw`,
      {
        amount: parseFloat(amount),
        senderAccountNumber,
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
    throw new Error(`Error withdrawing funds: ${error.message}`);
  }
};

export const postTransfer = async ({
  amount,
  senderAccountNumber,
  recipientAccountNumber,
}) => {
  try {
    const resp = await axios.post(
      `${API_URL}/api/v1/account/transactions/transfer`,
      {
        amount: parseFloat(amount),
        senderAccountNumber,
        recipientAccountNumber,
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
    throw new Error(`Error transferring funds: ${error.message}`);
  }
};
