import axios from 'axios';
import { useUserStore } from './stores/userStore';
<<<<<<< HEAD

=======
import { useRouter } from 'vue-router'

const router = useRouter();
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
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

    // 오류가 발생할 때 originalRequest.url을 로깅하여 확인
    console.log('Original request URL:', originalRequest.url);

<<<<<<< HEAD
    if (error.response.status === 400 && !originalRequest._retry) {
=======
    if (error.response.status === 403 && !originalRequest._retry && !userStore.userId != 0) {
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
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
<<<<<<< HEAD
=======
        userStore.userId = 0;
        userStore.accessToken = null;
        userStore.refreshToken = null;
        router.push({ name: 'HomeView' });
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;