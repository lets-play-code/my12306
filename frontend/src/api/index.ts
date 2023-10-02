import axios, { InternalAxiosRequestConfig } from 'axios'
import authentication from '@/services/authenticationService'
import { showMessage } from '@/main';

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 1000 * 30
})

instance.interceptors.response.use(
    res => {
        return Promise.resolve(res.data);
    },
    error => {
        if (error.response?.status === 401) {
            authentication.clearToken();
            showMessage('You have been logged out. Please login again.');
        }
        return Promise.reject(error);
    }
)

instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = authentication.getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

export default instance
