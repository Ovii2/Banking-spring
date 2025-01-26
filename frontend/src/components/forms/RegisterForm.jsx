import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { postRegister } from '../../services/post';
import { toast } from 'react-toastify';
import { useState } from 'react';

const RegisterForm = () => {
  const [error, setError] = useState('');

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    watch,
  } = useForm({
    defaultValues: {
      userName: '',
      email: '',
      password: '',
      // repeatPassword: '',
    },
  });

  const navigate = useNavigate();

  const formSubmitHandler = async (data) => {
    // if (existingUserEmail.includes(data.email)) {
    //   toast.error('A user with this email already exists!');
    //   return;
    // }

    try {
      const dataCopy = { ...data };
      // delete dataCopy['repeatPassword'];
      await postRegister(dataCopy);
      // updateUser();
      reset();
      toast.success('User created successfully!');
      navigate('/login');
    } catch (error) {
      toast.error('Error creating user');
    }
  };

  return (
    <div className='flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8'>
      <div className='sm:mx-auto sm:w-full sm:max-w-sm'>
        <img
          alt='Your Company'
          src='https://tailwindui.com/plus/img/logos/mark.svg?color=indigo&shade=600'
          className='mx-auto h-10 w-auto'
        />
        <h2 className='mt-10 text-center text-2xl/9 font-bold tracking-tight text-gray-900'>
          Register new account
        </h2>
      </div>

      <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-sm'>
        <form className='space-y-6' noValidate onSubmit={handleSubmit(formSubmitHandler)}>
          <div>
            <label htmlFor='email' className='block text-sm/6 font-medium text-gray-900'>
              Email address
            </label>
            <div className='mt-2'>
              <input
                {...register('email', {
                  required: 'email is required',
                })}
                id='email'
                name='email'
                type='email'
                required
                autoComplete='email'
                className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
              />
            </div>
          </div>

          <div>
            <label htmlFor='username' className='block text-sm/6 font-medium text-gray-900'>
              Username
            </label>
            <div className='mt-2'>
              <input
                {...register('username', {
                  required: 'username is required',
                })}
                id='username'
                name='username'
                type='username'
                required
                autoComplete='username'
                className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
              />
            </div>
          </div>

          <div>
            <div className='flex items-center justify-between'>
              <label htmlFor='password' className='block text-sm/6 font-medium text-gray-900'>
                Password
              </label>
              <div className='text-sm'>
                <a href='#' className='font-semibold text-indigo-600 hover:text-indigo-500'>
                  Forgot password?
                </a>
              </div>
            </div>
            <div className='mt-2'>
              <input
                {...register('password', {
                  required: 'password is required',
                })}
                id='password'
                name='password'
                type='password'
                required
                autoComplete='current-password'
                className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
              />
            </div>
          </div>

          <div>
            <button
              type='submit'
              className='flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600'
            >
              Register
            </button>
          </div>
        </form>

        <p className='mt-10 text-center text-sm/6 text-gray-500'>
          Not a member?{' '}
          <a href='#' className='font-semibold text-indigo-600 hover:text-indigo-500'>
            Start a 14 day free trial
          </a>
        </p>
      </div>
    </div>
  );
};

export default RegisterForm;
