import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
import axios from 'axios';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    teams: [],
    meetings: [],
    userList: [],
    userInfo: {},
    BACKEND_URL: 'http://localhost:5000/',
  }),
  actions: {
    async fetchUserTeamsAndMeetings(userId = 1) {
      this.userId = userId;

      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();
      
      try {
        const response = await axios.get(`${this.BACKEND_URL}api/users/${userId}`);
        const userData = response.data.data;
        this.userInfo = userData;

        const meetingIds = [];
        // teamList를 teamStore로 전달하여 개별 팀 정보 조회
        for (const teamId of userData.teamList) {
          const team = await teamStore.fetchTeamById(teamId);
          if (team) {
            meetingIds.push(...team.meetingList);
          }
        }

        // teamStore의 teams를 userStore의 teams로 설정
        this.teams = teamStore.teams;
        console.log('Teams:', this.teams);

        // meetingStore에서 meetings를 조회하여 userStore의 meetings로 설정
        await meetingStore.fetchMeetingsByIds(meetingIds);
        this.meetings = meetingStore.meetings;
        console.log('Meetings:', this.meetings);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    },
    
    async fetchAllUsers() {
      try {
        const response = await axios.get(`${this.BACKEND_URL}api/users`);
        this.userList = response.data;
        console.log('AllUsers', this.userList);
      } catch (error) {
        console.error('Failed to fetch all users:', error);
      }
    },
    
    async signUp({ id, idCheck, name, email, password, language }) {
      try {
        // 아이디 중복 체크
        const duplicationResponse = await axios.get(`${this.BACKEND_URL}api/users/duplication/${id}`);
        if (duplicationResponse.data.result.isAvailable) {
          // 회원가입 요청
          const signupResponse = await axios.post(`${this.BACKEND_URL}api/users`, {
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
        const response = await axios.post(`${this.BACKEND_URL}api/users/login`, { id, password });
        if (response.data.success) {
          this.userInfo = response.data.userInfo;
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

    // 아이디 중복 체크 메서드 추가
    async checkIdDuplication(id) {
      try {
        const response = await axios.get(`${this.BACKEND_URL}api/users/duplication/${id}`);
        return response.data.result.isAvailable;
      } catch (error) {
        console.error('Failed to check ID duplication:', error);
        throw new Error('Failed to check ID duplication');
      }
    }
  }
});
