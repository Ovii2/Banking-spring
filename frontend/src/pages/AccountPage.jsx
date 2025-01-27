import { useContext, useEffect, useState } from 'react';
import { getAccountDataByUserId, getUserTransactions } from '../services/get';
import { getUserIdFromToken } from '../utils/jwt';
import UserContext from '../context/UserContext';
import { postDeposit } from '../services/post';
import { toast } from 'react-toastify';

const AccountPage = () => {
  const { token } = useContext(UserContext);
  const [accountData, setAccountData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showDepositInput, setShowDepositInput] = useState(false);
  const [showWithdrawInput, setShowWithdrawInput] = useState(false);
  const [showTransferInput, setShowTransferInput] = useState(false);
  const [amount, setAmount] = useState('');
  const [targetAccount, setTargetAccount] = useState('');
  const [recipientAccount, setRecipientAccount] = useState('');
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    if (!token) {
      setError('User is not logged in');
      setLoading(false);
      return;
    }

    const userId = getUserIdFromToken(token);
    if (!userId) {
      setError('Invalid user token');
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        const [accountData, transactionsData] = await Promise.all([
          getAccountDataByUserId(userId),
          getUserTransactions(userId),
        ]);

        setAccountData(accountData);
        setTransactions(transactionsData);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [token, amount]);

  if (loading) return <p>Loading...</p>;
  // if (error) return <p className='text-red-500'>{error}</p>;

  const handleDeposit = async () => {
    try {
      const response = await postDeposit(amount, accountData.accountNumber);
      setAccountData({
        ...accountData,
        balance: response.balance,
      });
      setAmount('');
      setShowDepositInput(false);
      toast.success('Deposit successful');
    } catch (error) {
      toast.error('Error depositing funds');
      console.error(error);
    }
  };

  return (
    <>
      <main className='p-6 bg-gray-100 min-h-screen'>
        <div className='mb-8 bg-white rounded-lg shadow p-6'>
          <p className='text-2xl font-semibold text-gray-700'>Account Balance:</p>
          <p className='mt-4 text-3xl font-bold text-green-500'>
            {accountData.balance.toFixed(2)} EUR
          </p>
        </div>

        <div className='mb-8 bg-white rounded-lg shadow p-6'>
          <p className='text-lg text-gray-700'>
            {`${accountData.ownerName} Bank Account` || 'Bank Account'}
          </p>
          <p className='text-xl font-medium text-gray-900 mt-2'>{accountData.accountNumber}</p>
        </div>

        <div className='mb-8 bg-white rounded-lg shadow p-6 flex space-x-4'>
          <button
            onClick={() => {
              setShowDepositInput(!showDepositInput);
              setShowWithdrawInput(false);
              setShowTransferInput(false);
            }}
            className='px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600'
          >
            Deposit
          </button>
          <button
            onClick={() => {
              setShowWithdrawInput(!showWithdrawInput);
              setShowDepositInput(false);
              setShowTransferInput(false);
            }}
            className='px-6 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600'
          >
            Withdraw
          </button>
          <button
            onClick={() => {
              setShowTransferInput(!showTransferInput);
              setShowDepositInput(false);
              setShowWithdrawInput(false);
            }}
            className='px-6 py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-600'
          >
            Transfer
          </button>
        </div>

        {showDepositInput && (
          <div className='mb-8 bg-white rounded-lg shadow p-6'>
            <input
              type='number'
              min={0}
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className='w-2xl me-3 p-2 border rounded'
              placeholder='Enter deposit amount'
            />
            <button
              className='mt-4 px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600'
              onClick={handleDeposit}
            >
              Confirm Deposit
            </button>
          </div>
        )}

        {showWithdrawInput && (
          <div className='mb-8 bg-white rounded-lg shadow p-6'>
            <input
              type='number'
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className='w-2xl me-3 p-2 border rounded'
              placeholder='Enter withdraw amount'
            />
            <button
              className='mt-4 px-6 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600'
              onClick={() => {}}
            >
              Confirm Withdraw
            </button>
          </div>
        )}

        {showTransferInput && (
          <div className='mb-8 bg-white rounded-lg shadow p-6'>
            <input
              type='number'
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className='w-2xl me-3 p-2 border rounded'
              placeholder='Enter transfer amount'
            />
            <input
              type='text'
              value={recipientAccount}
              onChange={(e) => setRecipientAccount(e.target.value)}
              className='w-2xl me-3 p-2 border rounded'
              placeholder='Enter recipient account number'
            />
            <button
              className='mt-4 px-6 py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-600'
              onClick={() => {}}
            >
              Confirm Transfer
            </button>
          </div>
        )}

        <div className='bg-white rounded-lg shadow p-6'>
          <table className='w-full border-collapse border border-gray-200'>
            <thead>
              <tr className='bg-gray-100'>
                <th className='border border-gray-300 px-4 py-2 text-left font-semibold text-gray-700'>
                  Amount
                </th>
                <th className='border border-gray-300 px-4 py-2 text-left font-semibold text-gray-700'>
                  Account Number
                </th>
                <th className='border border-gray-300 px-4 py-2 text-left font-semibold text-gray-700'>
                  Transaction Date
                </th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => {
                const formatDate = (dateString) => {
                  const date = new Date(dateString);
                  const year = date.getFullYear();
                  const month = String(date.getMonth() + 1).padStart(2, '0');
                  const day = String(date.getDate()).padStart(2, '0');
                  const hours = String(date.getHours()).padStart(2, '0');
                  const minutes = String(date.getMinutes()).padStart(2, '0');
                  const seconds = String(date.getSeconds()).padStart(2, '0');

                  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
                };

                return (
                  <tr key={transaction.transactionId}>
                    <td
                      className={`border border-gray-300 px-4 py-2 ${
                        transaction.transactionType === 'DEPOSIT'
                          ? 'text-green-500'
                          : 'text-red-500'
                      }`}
                    >
                      {transaction.transactionType === 'DEPOSIT'
                        ? `+${transaction.amount.toFixed(2)}`
                        : `-${transaction.amount.toFixed(2)}`}{' '}
                      EUR
                    </td>
                    <td className='border border-gray-300 px-4 py-2 text-gray-600'>
                      {transaction.accountNumber}
                    </td>
                    <td className='border border-gray-300 px-4 py-2 text-gray-600'>
                      {formatDate(transaction.transactionDate)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </main>
    </>
  );
};

export default AccountPage;
