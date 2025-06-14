import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useForm } from 'react-hook-form';
import { loginPost } from '../../services/post';
import UserContext from '../../context/UserContext';

const LoginForm = () => {
  const [error, setError] = useState('');
  const { setToken } = useContext(UserContext);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: '',
      password: '',
    },
  });

  const navigate = useNavigate();

  const formSubmitHandler = async (data) => {
    try {
      const response = await loginPost(data);
      localStorage.setItem('token', response.token);
      setToken(response.token);
      reset();
      toast.success('Login successfull!');
      window.location.replace('/account');
    } catch (error) {
      toast.error('Invalid email or password');
    }
  };

  return (
    <>
      <div className='flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8'>
        <div className='sm:mx-auto sm:w-full sm:max-w-sm'>
          <img
            alt='Company logo'
            // src='https://tailwindui.com/plus/img/logos/mark.svg?color=indigo&shade=600'
            src='/src/assets/icons/logo.png'
            className='mx-auto h-10 w-auto'
          />
          <h2 className='mt-10 text-center text-2xl/9 font-bold tracking-tight text-gray-900'>
            Log in
          </h2>
        </div>

        <div className='mt-10 sm:mx-auto sm:w-full sm:max-w-sm'>
          <form
            onSubmit={handleSubmit(formSubmitHandler)}
            // method='POST'
            className='space-y-6'
            noValidate
          >
            {/* <div>
              <label htmlFor='email' className='block text-sm/6 font-medium text-gray-900'>
                Email address
              </label>
              <div className='mt-2'>
                <input
                  {...register('email', {
                    required: 'Email is required',
                  })}
                  id='email'
                  name='email'
                  type='email'
                  required
                  autoComplete='email'
                  className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
                />
              </div>
              {errors.email && <div className='invalid-feedback'>{errors.email.message}</div>}
            </div> */}

            <div>
              <label
                htmlFor='username'
                className='block text-sm/6 font-medium text-gray-900'
              >
                Username
              </label>
              <div className='mt-2'>
                <input
                  {...register('username', {
                    required: 'Username is required',
                  })}
                  id='username'
                  name='username'
                  type='username'
                  required
                  autoComplete='username'
                  className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
                />
              </div>
              {errors.username && (
                <div className='text-red-700'>{errors.username.message}</div>
              )}
            </div>

            <div>
              <div className='flex items-center justify-between'>
                <label
                  htmlFor='password'
                  className='block text-sm/6 font-medium text-gray-900'
                >
                  Password
                </label>
                <div className='text-sm'>
                  <a
                    href='#'
                    className='font-semibold text-indigo-600 hover:text-indigo-500'
                  >
                    Forgot password?
                  </a>
                </div>
              </div>
              <div className='mt-2'>
                <input
                  {...register('password', {
                    required: 'Password is required',
                  })}
                  id='password'
                  name='password'
                  type='password'
                  required
                  autoComplete='current-password'
                  className='block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6'
                />
              </div>
              {errors.password && (
                <div className='text-red-700'>{errors.password.message}</div>
              )}
            </div>

            <div>
              <button
                type='submit'
                className='flex w-full justify-center rounded-md bg-emerald-600 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-emerald-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-500'
              >
                Sign in
              </button>
            </div>
          </form>

          <p className='mt-10 text-center text-sm/6 text-gray-500'>
            Don't have an account?{' '}
            <a
              href='/register'
              className='font-semibold text-indigo-600 hover:text-indigo-500'
            >
              Register
            </a>
          </p>
        </div>
      </div>
    </>
  );
};

export default LoginForm;
