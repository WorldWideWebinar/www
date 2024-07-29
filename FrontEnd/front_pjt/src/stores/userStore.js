import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
import axiosInstance from '@/axios';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 0,
    teams: [],
    meetings: [],
    userList: [],
    userInfo: {},
    accessToken: null,
    refreshToken: null,
  }),
  getters: {
    isLogin: (state) => !!state.accessToken,
  },
  actions: {
    async fetchUserTeamsAndMeetings(userId = 1) {
      this.userId = userId;

      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();
      
      try {
        const response = await axiosInstance.get(`api/users/${userId}`);
        const userData = response.data.data;
        this.userInfo = userData;

        const meetingIds = [];
        for (const teamId of userData.teamList) {
          const team = await teamStore.fetchTeamById(teamId);
          if (team) {
            meetingIds.push(...team.meetingList);
          }
        }

        this.teams = teamStore.teams;
        console.log('Teams:', this.teams);

        await meetingStore.fetchMeetingsByIds(meetingIds);
        this.meetings = meetingStore.meetings;
        console.log('Meetings:', this.meetings);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    },
    
    async fetchAllUsers() {
      try {
        const response = await axiosInstance.get(`api/users`);
        this.userList = response.data;
        console.log('AllUsers', this.userList);
      } catch (error) {
        console.error('Failed to fetch all users:', error);
      }
    },
    
    async signUp({ id, idCheck, name, email, password, language }) {
      try {
        const duplicationResponse = await axiosInstance.get(`api/users/duplication/${id}`);
        if (duplicationResponse.data.result.isAvailable) {
          const signupResponse = await axiosInstance.post(`api/users`, {
            id,
            idCheck,
            name,
            email,
            password,
            language
          });
          return signupResponse.data;
        } else {
          console.log('ID is not available');
          return { isSuccess: false, message: 'ID is not available' };
        }
      } catch (error) {
        console.error('Failed to sign up:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async signIn({ id, password }) {
      try {
        const response = await axiosInstance.post(`api/users/login`, { id, password });
        if (response.data.success) {
          this.userInfo = response.data.userInfo;
          this.accessToken = response.data.accessToken;
          this.refreshToken = response.data.refreshToken;
          console.log('User signed in:', this.userInfo);
          return { isSuccess: true, data: response.data.userInfo };
        } else {
          console.log('Login failed:', response.data.message);
          return { isSuccess: false, message: response.data.message };
        }
      } catch (error) {
        console.error('Failed to sign in:', error);
        return { isSuccess: false, message: error.message };
      }
    },

    async checkIdDuplication(id) {
      try {
        const response = await axiosInstance.get(`api/users/duplication/${id}`);
        return response.data.result.isAvailable;
      } catch (error) {
        console.error('Failed to check ID duplication:', error);
        throw new Error('Failed to check ID duplication');
      }
    }
  }
});
