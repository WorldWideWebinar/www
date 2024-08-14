import axios from 'axios';
import { useUserStore } from './stores/userStore';
import { useRouter } from 'vue-router'

const router = useRouter();
const axiosInstance = axios.create({
  baseURL: 'https://i11a501.p.ssafy.io/', // baseURL이 올바르게 설정되어 있는지 확인
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

    if (error.response.status == 403 && !originalRequest._retry && !userStore.userId != 0) {
      originalRequest._retry = true;
      try {
        const response = await axios.get(
          'https://i11a501.p.ssafy.io/api/users/refresh',
          {
            headers: { Authorization: `Bearer ${userStore.refreshToken}` },
          }
        );

        const { accessToken, refreshToken } = response.data.result.jwt;
        userStore.accessToken = accessToken;
        userStore.refreshToken = refreshToken;

        axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

        // originalRequest.url을 확인하고 수정
        originalRequest.url = originalRequest.url.replace('undefined', '');

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        userStore.userId = 0;
        userStore.accessToken = null;
        userStore.refreshToken = null;
        router.push({ name: 'HomeView' });
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;