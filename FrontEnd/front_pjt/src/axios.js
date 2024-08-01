import axios from 'axios';
import { useUserStore } from './stores/userStore';

const axiosInstance = axios.create({
  baseURL: 'https://i11a501.p.ssafy.io/',
});

axiosInstance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const userStore = useUserStore();
    const originalRequest = error.config;

    if (error.response.status === 400 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const response = await axios.post(
          `${userStore.BACKEND_URL}api/users/refresh`,
          {},
          {
            headers: { Authorization: `Bearer ${userStore.refreshToken}` },
          }
        );

        const { accessToken, refreshToken } = response.data.result.jwt;
        userStore.accessToken = accessToken;
        userStore.refreshToken = refreshToken;

        axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
