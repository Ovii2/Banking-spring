import { useContext, useEffect, useRef, useState } from 'react';
import { getAccountDataByUserId, getUserTransactions } from '../services/get';
import { getUserIdFromToken } from '../utils/jwt';
import UserContext from '../context/UserContext';
import { postDeposit, postTransfer, postWithdraw } from '../services/post';
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

  const refreshIntervalRef = useRef(null);

  const fetchData = async () => {
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

    try {
      const [accountData, transactionsData] = await Promise.all([
        getAccountDataByUserId(userId),
        getUserTransactions(userId),
      ]);

      setAccountData(accountData);
      setTransactions(transactionsData);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();

    refreshIntervalRef.current = setInterval(fetchData, 5000);

    return () => {
      if (refreshIntervalRef.current) {
        clearInterval(refreshIntervalRef.current);
      }
    };
  }, [token]);

  if (loading) return <p>Loading...</p>;
  if (!accountData) return <p>No account data available</p>;

  const handleDeposit = async () => {
    try {
      const updatedBalance = accountData.balance + parseFloat(amount);
      setAccountData({
        ...accountData,
        balance: updatedBalance,
      });

      const response = await postDeposit(amount, accountData.accountNumber);

      setAccountData({
        ...accountData,
        balance: response.balance,
      });
      setAmount('');
      setShowDepositInput(false);
      toast.success('Deposit successful');

      fetchData();
    } catch (error) {
      setAccountData({
        ...accountData,
        balance: accountData.balance,
      });
      toast.error('Error depositing funds');
      console.error(error);
    }
  };

  const handleWithdraw = async () => {
    try {
      const updatedBalance = accountData.balance - parseFloat(amount);
      setAccountData({
        ...accountData,
        balance: updatedBalance,
      });

      const response = await postWithdraw(amount, accountData.accountNumber);

      setAccountData({
        ...accountData,
        balance: response.balance,
      });
      setAmount('');
      setShowWithdrawInput(false);
      toast.success('Withdraw successful');

      fetchData();
    } catch (error) {
      setAccountData({
        ...accountData,
        balance: accountData.balance,
      });
      toast.error('Error withdrawing funds');
      console.error(error);
    }
  };

  const handleTransfer = async () => {
    try {
      const updatedBalance = accountData.balance - parseFloat(amount);
      setAccountData({
        ...accountData,
        balance: updatedBalance,
      });

      const response = await postTransfer(amount, accountData.accountNumber, recipientAccount);
      setAccountData({
        ...accountData,
        balance: response.balance,
      });
      setAmount('');
      setRecipientAccount('');
      setShowTransferInput(false);
      toast.success('Transfer successful');

      fetchData();
    } catch (error) {
      setAccountData({
        ...accountData,
        balance: accountData.balance,
      });
      toast.error('Error transferring funds');
      console.error(error);
    }
  };

  return (
    <>
      <main className='p-4 sm:p-6 bg-gray-100 min-h-screen'>
        {/* Balance Card */}
        <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
          <p className='text-xl sm:text-2xl font-semibold text-gray-700'>Account Balance:</p>
          <p className='mt-2 sm:mt-4 text-2xl sm:text-3xl font-bold text-green-500'>
            {accountData?.balance?.toFixed(2)} EUR
          </p>
        </div>

        {/* Account Info Card */}
        <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
          <p className='text-base sm:text-lg text-gray-700'>
            {`${accountData?.ownerName} Bank Account` || 'Bank Account'}
          </p>
          <p className='text-lg sm:text-xl font-medium text-gray-900 mt-2'>
            {accountData?.accountNumber}
          </p>
        </div>

        {/* Action Buttons */}
        <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
          <div className='flex flex-col sm:flex-row gap-2 sm:space-x-4'>
            <button
              onClick={() => {
                setShowDepositInput(!showDepositInput);
                setShowWithdrawInput(false);
                setShowTransferInput(false);
              }}
              className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600'
            >
              Deposit
            </button>
            <button
              onClick={() => {
                setShowWithdrawInput(!showWithdrawInput);
                setShowDepositInput(false);
                setShowTransferInput(false);
              }}
              className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600'
            >
              Withdraw
            </button>
            <button
              onClick={() => {
                setShowTransferInput(!showTransferInput);
                setShowDepositInput(false);
                setShowWithdrawInput(false);
              }}
              className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-600'
            >
              Transfer
            </button>
          </div>
        </div>

        {/* Input Forms */}
        {showDepositInput && (
          <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
            <div className='flex flex-col sm:flex-row gap-2 sm:gap-4'>
              <input
                type='number'
                min={0}
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className='w-full sm:w-auto p-2 border rounded'
                placeholder='Enter deposit amount'
              />
              <button
                className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600'
                onClick={handleDeposit}
              >
                Confirm Deposit
              </button>
            </div>
          </div>
        )}

        {showWithdrawInput && (
          <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
            <div className='flex flex-col sm:flex-row gap-2 sm:gap-4'>
              <input
                type='number'
                min={0}
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className='w-full sm:w-auto p-2 border rounded'
                placeholder='Enter withdraw amount'
              />
              <button
                className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600'
                onClick={handleWithdraw}
              >
                Confirm Withdraw
              </button>
            </div>
          </div>
        )}

        {showTransferInput && (
          <div className='mb-4 sm:mb-8 bg-white rounded-lg shadow p-4 sm:p-6'>
            <div className='flex flex-col gap-2 max-w-md'>
              <input
                type='number'
                min={0}
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className='w-full p-2 border rounded'
                placeholder='Enter transfer amount'
              />
              <input
                type='text'
                value={recipientAccount}
                onChange={(e) => setRecipientAccount(e.target.value)}
                className='w-full p-2 border rounded'
                placeholder='Enter recipient account number'
              />
              <button
                className='w-full sm:w-auto px-4 sm:px-6 py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-600'
                onClick={handleTransfer}
              >
                Confirm Transfer
              </button>
            </div>
          </div>
        )}

        {/* Transactions Table */}
        <div className='bg-white rounded-lg shadow p-4 sm:p-6 overflow-x-auto'>
          <table className='w-full min-w-[640px] border-collapse border border-gray-200'>
            <thead>
              <tr className='bg-gray-100'>
                <th className='border border-gray-300 px-2 sm:px-4 py-2 text-left font-semibold text-gray-700'>
                  Amount
                </th>
                <th className='border border-gray-300 px-2 sm:px-4 py-2 text-left font-semibold text-gray-700'>
                  Account Number
                </th>
                <th className='border border-gray-300 px-2 sm:px-4 py-2 text-left font-semibold text-gray-700'>
                  Transaction Date
                </th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => {
                const formatDate = (dateString) => {
                  return new Date(dateString).toLocaleString('lt-LT', {
                    timeZone: 'Europe/Vilnius',
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit',
                    hour12: false,
                  });
                };

                return (
                  <tr key={transaction.transactionId}>
                    <td
                      className={`border border-gray-300 px-2 sm:px-4 py-2 ${
                        transaction.transactionType === 'DEPOSIT' ||
                        transaction.transactionType === 'TRANSFER_IN'
                          ? 'text-green-500'
                          : 'text-red-500'
                      }`}
                    >
                      {transaction.transactionType === 'DEPOSIT' ||
                      transaction.transactionType === 'TRANSFER_IN'
                        ? `+${transaction.amount.toFixed(2)}`
                        : `-${transaction.amount.toFixed(2)}`}{' '}
                      EUR
                    </td>
                    <td className='border border-gray-300 px-2 sm:px-4 py-2 text-gray-600'>
                      {transaction.transactionType === 'TRANSFER_OUT'
                        ? transaction.recipientAccountNumber
                        : transaction.transactionType === 'TRANSFER_IN'
                        ? transaction.recipientAccountNumber
                        : transaction.senderAccountNumber}
                    </td>
                    <td className='border border-gray-300 px-2 sm:px-4 py-2 text-gray-600'>
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
