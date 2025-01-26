import { jwtDecode } from 'jwt-decode';

export const getUserIdFromToken = (token) => {
  try {
    const decodedToken = jwtDecode(token);
    return decodedToken.id;
  } catch (error) {
    console.error('Invalid token', error);
    return null;
  }
};
