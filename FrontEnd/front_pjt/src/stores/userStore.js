import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
import axios from 'axios';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:5000/',
});

// 요청 인터셉터 추가
api.interceptors.request.use(
  config => {
    const store = useUserStore();
    if (store.accessToken) {
      config.headers.Authorization = `Bearer ${store.accessToken}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 응답 인터셉터 추가
api.interceptors.response.use(
  response => response,
  async error => {
    const store = useUserStore();
    const originalRequest = error.config;

    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const response = await store.refreshToken();
      if (response.isSuccess) {
        api.defaults.headers.common['Authorization'] = `Bearer ${store.accessToken}`;
        return api(originalRequest);
      }
    }
    return Promise.reject(error);
  }
);

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    teams: [],
    meetings: [],
    userList: [],
    userInfo: {},
    profileImageUrl: '',
    totalMeetingTime: 0,
    teamList: [],
    accessToken: '',
    refreshToken: '',
    BACKEND_URL: 'http://localhost:5000/',
  }),
  actions: {
    async fetchUserTeamsAndMeetings(userId = 1) {
      this.userId = userId;

      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();
      const userData = {
        id: 1,
        idCheck: true,
        name: "주연수",
        email: "jooys130@naver.com",
        password: "1234",
        profileImageUrl: "http://asdasdad.com/rads.png",
        totalMeetingTime: 135,
        teamList: [1, 2, 3, 4]
      };
      this.userInfo = userData;

      const meetingIds = [];
      for (const teamId of userData.teamList) {
        const team = await teamStore.fetchTeamById(teamId);
        if (team) {
          meetingIds.push(...team.meetingList);
        }
      }

      this.teams = teamStore.teams;
      await meetingStore.fetchMeetingsByIds(meetingIds);
      this.meetings = meetingStore.meetings;
    },
    
    async fetchAllUsers() {
      this.userList = [
        { id: 1, username: 'alice', email: 'alice@example.com' },
        { id: 2, username: 'bob', email: 'bob@google.com' },
        { id: 3, username: 'charlie', email: 'charlie@naver.com' },
        { id: 4, username: 'david', email: 'david@daum.net' },
        { id: 5, username: 'eve', email: 'eve@example.com' },
        { id: 6, username: 'frank', email: 'frank@google.com' },
        { id: 7, username: 'grace', email: 'grace@naver.com' },
        { id: 8, username: 'heidi', email: 'heidi@daum.net' },
        { id: 9, username: 'ivan', email: 'ivan@example.com' },
        { id: 10, username: 'judy', email: 'judy@google.com' }
      ];
      console.log('AllUsers', this.userList);
    },
    
    async checkIdDuplication(id) {
      try {
        const response = await api.get(`api/users/duplication/${id}`);
        return response.data.result.isAvailable;
      } catch (error) {
        console.error('Failed to check ID duplication:', error);
        throw new Error(error.message);
      }
    },

    async signUp({ id, idCheck, name, email, password, language }) {
      try {
        if (!idCheck) {
          return { isSuccess: false, message: 'ID has not been checked for duplication' };
        }
        
        const signupResponse = await api.post(`api/users`, {
          id,
          idCheck,
          name,
          email,
          password,
          language
        });
        return signupResponse.data;
      } catch (error) {
        console.error('Failed to sign up:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async signIn({ id, password }) {
      try {
        const response = await api.post(`api/users/login`, { id, password });
        if (response.data.success) {
          const result = response.data.result;
          this.accessToken = result.jwt.accessToken;
          this.refreshToken = result.jwt.refreshToken;
          this.userId = result.userId;
          console.log('User signed in:', this.userInfo);

          // Fetch user data after successful sign-in
          const userResponse = await api.get(`api/users/${this.userId}`);
          const userData = userResponse.data.data;

          // Update state with user data
          this.userInfo = {
            id: userData.id,
            name: userData.name,
            email: userData.email,
            profileImageUrl: userData.profileImageUrl,
            totalMeetingTime: userData.totalMeetingTime,
            teamList: userData.teamList
          };
          this.profileImageUrl = userData.profileImageUrl;
          this.totalMeetingTime = userData.totalMeetingTime;
          this.teamList = userData.teamList;

          return { isSuccess: true, data: userData };
        } else {
          console.log('Login failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to sign in:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async refreshToken() {
      try {
        const response = await api.post(`api/users/refresh`, { refreshToken: this.refreshToken });
        if (response.data.isSuccess) {
          this.accessToken = response.data.result.jwt.accessToken;
          this.refreshToken = response.data.result.jwt.refreshToken;
          return { isSuccess: true };
        } else {
          console.log('Refresh token failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to refresh token:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async signOut() {
      try {
        const response = await api.post(`api/users/logout`);
        if (response.data.success) {
          this.userInfo = {};
          this.profileImageUrl = '';
          this.totalMeetingTime = 0;
          this.teamList = [];
          this.accessToken = '';
          this.refreshToken = '';
          console.log('User signed out');
          return { isSuccess: true };
        } else {
          console.log('Logout failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to sign out:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async updateUserProfile({ userId, email, password, language, profileImageUrl }) {
      try {
        const response = await api.put(`api/users/${userId}`, {
          email,
          password,
          language,
          profileImageUrl
        });
        if (response.data.success) {
          this.userInfo = { ...this.userInfo, email, password, language, profileImageUrl };
          console.log('User profile updated:', this.userInfo);
          return { isSuccess: true, data: this.userInfo };
        } else {
          console.log('Update failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to update profile:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async deleteUser(userId) {
      try {
        const response = await api.delete(`api/users/${userId}`);
        if (response.data.success) {
          this.userInfo = {};
          this.profileImageUrl = '';
          this.totalMeetingTime = 0;
          this.teamList = [];
          this.accessToken = '';
          this.refreshToken = '';
          console.log('User deleted');
          return { isSuccess: true };
        } else {
          console.log('Delete failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to delete user:', error);
        return { isSuccess: false, message: error.message };
      }
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'user',
        storage: localStorage,
      },
    ],
  },
});
