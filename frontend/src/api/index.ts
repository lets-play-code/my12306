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
            authentication.clearToken();
            // 如果不是登录页面，跳转到登录
            if (!window.location.pathname.includes('/login')) {
                showMessage('登录已过期，请重新登录');
                window.location.href = '/login';
            }
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
