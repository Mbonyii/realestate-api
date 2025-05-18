import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

// Types
interface ApiError {
  message: string;
  status?: number;
  data?: any;
}

interface TokenResponse {
  token: string;
  type: string;
}

// Get base URL from environment variable or use default
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const apiClient: AxiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true // Enable sending cookies in cross-origin requests
});

// Token management
const getToken = (): string | null => {
  return localStorage.getItem('token');
};

const setToken = (token: string): void => {
  localStorage.setItem('token', token);
};

const removeToken = (): void => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

// Request interceptor
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig): AxiosRequestConfig => {
    // Add auth token if available
    const token = getToken();
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    // Log request in development
    if (import.meta.env.DEV) {
      console.log(`🚀 [${config.method?.toUpperCase()}] ${config.url}`, {
        headers: config.headers,
        data: config.data
      });
    }

    return config;
  },
  (error: AxiosError): Promise<never> => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => {
    // Log response in development
    if (import.meta.env.DEV) {
      console.log(`✅ [${response.status}] ${response.config.url}`, response.data);
    }

    // Store token if it's a login/signup response
    if (response.data?.token) {
      setToken(response.data.token);
    }

    return response;
  },
  async (error: AxiosError<ApiError>): Promise<never> => {
    const originalRequest = error.config;
    
    // Handle 401 Unauthorized
    if (error.response?.status === 401) {
      // Only redirect to login if not already trying to access auth endpoints
      if (originalRequest?.url && !originalRequest.url.includes('/auth/')) {
        removeToken();
        window.location.href = '/login';
      }
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      window.location.href = '/unauthorized';
    }

    // Handle 404 Not Found
    if (error.response?.status === 404) {
      console.error('Resource not found:', originalRequest?.url);
    }

    // Handle 500 Server Error
    if (error.response?.status === 500) {
      console.error('Server error:', error.response.data);
    }

    // Log error details
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      console.error('API Error Response:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers,
        url: originalRequest?.url
      });
    } else if (error.request) {
      // The request was made but no response was received
      console.error('No response received:', error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error('Request setup error:', error.message);
    }

    // Create a standardized error object
    const apiError: ApiError = {
      message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      status: error.response?.status,
      data: error.response?.data
    };

    return Promise.reject(apiError);
  }
);

// Export the configured client
export default apiClient;

// Export types and utilities
export type { ApiError, TokenResponse };
export { getToken, setToken, removeToken }; 