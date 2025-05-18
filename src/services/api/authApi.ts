import axios, { AxiosError, AxiosRequestConfig } from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Default axios configuration
const defaultConfig: AxiosRequestConfig = {
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true
};

// Response interfaces
export interface LoginRequest {
  email: string;
  password: string;
}

export interface TwoFactorVerificationRequest {
  token: string;
  code: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  twoFactorEnabled: boolean;
  twoFactorQrCodeUri?: string;
  authenticated: boolean;
}

export interface SignupRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone?: string;
  address?: string;
  role: string;
  enableTwoFactor: boolean;
}

export interface ErrorResponse {
  message: string;
  success: boolean;
}

export interface MessageResponse {
  message: string;
  success: boolean;
}

// Error handling utility
const handleApiError = (error: unknown): never => {
  console.error('API Error:', error);
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ErrorResponse>;
    throw new Error(axiosError.response?.data?.message || 'Request failed');
  }
  throw new Error('Network error occurred');
};

const authApi = {
  login: async (loginRequest: LoginRequest): Promise<JwtResponse> => {
    try {
      console.log('Making login request to:', `${API_URL}/auth/login`);
      const response = await axios.post<JwtResponse>(
        `${API_URL}/auth/login`,
        loginRequest,
        defaultConfig
      );
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },

  verifyTwoFactor: async (verificationRequest: TwoFactorVerificationRequest): Promise<JwtResponse> => {
    try {
      console.log('Making 2FA verification request to:', `${API_URL}/auth/verify-2fa`);
      const response = await axios.post<JwtResponse>(
        `${API_URL}/auth/verify-2fa`,
        verificationRequest,
        defaultConfig
      );
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },

  forgotPassword: async (email: string): Promise<MessageResponse> => {
    try {
      console.log('Making forgot password request to:', `${API_URL}/auth/forgot-password`);
      const response = await axios.post<MessageResponse>(
        `${API_URL}/auth/forgot-password`,
        { email },
        defaultConfig
      );
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },

  resetPassword: async (resetRequest: ResetPasswordRequest): Promise<MessageResponse> => {
    try {
      console.log('Making reset password request to:', `${API_URL}/auth/reset-password`);
      const response = await axios.post<MessageResponse>(
        `${API_URL}/auth/reset-password`,
        resetRequest,
        defaultConfig
      );
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },

  signup: async (signupRequest: SignupRequest): Promise<MessageResponse> => {
    try {
      console.log('Making signup request to:', `${API_URL}/auth/signup`);
      console.log('Signup data:', { ...signupRequest, password: '******' }); // Hide password in logs
      
      const response = await axios.post<MessageResponse>(
        `${API_URL}/auth/signup`,
        signupRequest,
        defaultConfig
      );
      
      console.log('Signup response:', response.data);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  }
};

// Log the API URL on initialization
console.log('AuthApi initialized with API_URL:', API_URL);

export default authApi; 