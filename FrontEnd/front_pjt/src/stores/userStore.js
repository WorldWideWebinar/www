import { defineStore } from 'pinia';
import axiosInstance from '@/axios';
import { useTeamStore } from './teamStore';
import router from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 0,
    userInfo: {},
    accessToken: null,
    refreshToken: null,
  }),
  getters: {
    isLogin: (state) => !!state.accessToken,
  },
  actions: {
    async fetchUserInfo(userId) {
      try {
        const response = await axiosInstance.get(`api/users/${userId}`);
        const userData = response.data.data;
        this.userInfo = userData;
        return userData;
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    },

    async signIn({ id, password }) {
      try {
        const response = await axiosInstance.post('api/users/login', { id, password });
        console.log(response.data);
        if (response.data.code === 200 && response.data.result.userId) {
          const { userId, jwt } = response.data.result;
          this.userId = userId;
          this.accessToken = jwt.accessToken;
          this.refreshToken = jwt.refreshToken;

          // 사용자 정보 가져오기
          const userInfo = await this.fetchUserInfo(userId);
          if (userInfo) {
            console.log('User signed in:', this.userInfo);

            // 팀 정보 가져오기
            const teamStore = useTeamStore();
            await teamStore.fetchUserTeams(this.userInfo.teamList);

            // 로그인 성공 후 HomeView로 리디렉션
            router.push({ name: 'HomeView' });

            return { isSuccess: true, data: this.userInfo };
          } else {
            return { isSuccess: false, message: 'Failed to fetch user info' };
          }
        } else {
          console.log('Login failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to sign in:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async signUp({ id, name, email, password, language }) {
      try {
        const signupResponse = await axiosInstance.post(`api/users`, {
          id,
          name,
          idCheck:true,
          email,
          password,
          language
        });
        return signupResponse.data.result;
      } catch (error) {
        console.error('Failed to sign up:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async signOut() {
      try {
        const response = await axiosInstance.post(`api/users/logout`, { id: this.userId });
        console.log(response.data)
        if (response.data.isSuccess) {
          // 사용자 정보를 초기화
          this.userId = 0;
          this.userInfo = {};
          this.accessToken = null;
          this.refreshToken = null;
          console.log('User signed out successfully');
          return { isSuccess: true, message: response.data.message };
        } else {
          console.log('Logout failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to sign out:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async checkIdDuplication(id) {
      try {
        const response = await axiosInstance.get(`api/users/duplication/${id}`);
        return response.data.result.available;
      } catch (error) {
        console.error('Failed to check ID duplication:', error);
        throw new Error('ID already exists.');
      }
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'userStore',
        storage: localStorage,
        paths: ['userId', 'userInfo', 'accessToken', 'refreshToken']
      }
    ]
  }
});

