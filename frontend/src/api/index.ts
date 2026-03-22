import axios, { InternalAxiosRequestConfig } from 'axios'
import authentication from '@/services/authenticationService'
import { showMessage } from '@/main';

const instance = axios.create({
    baseURL: '/api',
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
            const message = error.response?.data?.message || '请先登录';
            authentication.clearToken();
            showMessage(message);
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
