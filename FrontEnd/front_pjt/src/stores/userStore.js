import { defineStore } from 'pinia';
import axiosInstance from '@/axios';
import { useTeamStore } from './teamStore';
import router from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 0,
    userInfo: {},
    accessToken: 1, /* 로그인 */
    refreshToken: null,
    userList: [],
  }),
  getters: {
    isLogin: (state) => !!state.accessToken,
  },
  
  actions: {
    async fetchUserInfo(userId) {
      try {
        const response = await axiosInstance.get(`api/users/${userId}`);
        const userData = response.data;
        this.userInfo = userData;
        console.log(response.data)
        return userData;
      } catch (error) {
        this.showError('Failed to fetch user info');
      }
    },

    async fetchAllUsers(){
      try {
        const response = await axiosInstance.get(`api/users`);
        const users = response.data.result
        console.log(response.data)
        this.userList = users.map(user => ({
          id: user.userId,
          username: user.id,
        }));
        console.log(this.userList);
        return this.userList;
      } catch (error) {
        console.log(error);
      }
    },

    async signIn({ id, password }) {
      try {
        const response = await axiosInstance.post('api/users/login', { id, password });
        console.log(response.data);
        if (response.data.result.userId) {
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
            if (Array.isArray(userInfo.teamList) && userInfo.teamList.length > 0) {
              await Promise.all(
                userInfo.teamList.map(teamId => teamStore.fetchTeamById(teamId))
              );
            }

            // 로그인 성공 후 HomeView로 리디렉션
            router.push({ name: 'HomeView' });

            return { success: true, message: response.data.message };
          } else {
            return { success: false, message: 'Failed to fetch user info' };
          }
        } else {
          this.showError(response.data.message);
          return { success: false, message: response.data.message };
        }
      } catch (error) {
        this.showError('Failed to sign in');
        return { success: false, message: error.message };
      }
    },

    async signUp({ id, name, email, password, language }) {
      try {
        const signupResponse = await axiosInstance.post('api/users', {
          id,
          name,
          idCheck: true,
          email,
          password,
          language
        });
        console.log(signupResponse)
        if (signupResponse.data.success) {
          return { success: true, result: signupResponse.data.result };
        } else {
          return { success: false, message: signupResponse.data.message };
        }
      } catch (error) {
        console.log('here')
        return { success: false, message: error.message };
      }
    },

    async signOut() {
      try {
        const headers = {
          Authorization: `Bearer ${this.accessToken}`
        };
        console.log('Request Headers:', headers);
        console.log('userId' ,this.userId);
        const response = await axiosInstance.post('api/users/logout', { userId: this.userId });
  
        if (response.data.success) {
          // 사용자 정보를 초기화
          this.userId = 0;
          this.userInfo = {};
          this.accessToken = null;
          this.refreshToken = null;
          console.log('User signed out successfully');
          return { success: true, message: response.data.message };
        } else {
          return { success: false, message: response.data.message };
        }
      } catch (error) {

        return { success: false, message: error.message };
      }
    },

    async checkIdDuplication(id) {
      try {
        const response = await axiosInstance.get(`api/users/duplication/${id}`);
        return response.data.result.available;
      } catch (error) {
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
