import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
// import axios from 'axios';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    teams: [],
    meetings: [],
    userList: [],
    userInfo: {},
  }),
  actions: {
    async fetchUserTeamsAndMeetings(userId = 1) {
      this.userId = userId;

      const teamStore = useTeamStore();

      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`http://localhost:8000/api/users/${userId}`);
      //   const userData = response.data.data;
      //   this.userInfo = userData;

      //   // teamList를 teamStore로 전달하여 개별 팀 정보 조회
      //   for (const teamId of userData.teamList) {
      //     await teamStore.fetchTeamById(teamId);
      //   }

      //   // userStore의 teams를 teamStore의 userTeams로 설정
      //   this.teams = teamStore.userTeams;

      //   // meetings 정보를 추가적으로 조회하는 API가 있으면 호출
      //   // await meetingStore.fetchAllMeetingsByUser(userId);
      //   // this.meetings = meetingStore.userMeetings;
      // } catch (error) {
      //   console.error('Failed to fetch user info:', error);
      // }

      // 임시 데이터 사용
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

      // teamList를 teamStore로 전달하여 개별 팀 정보 조회
      for (const teamId of userData.teamList) {
        await teamStore.fetchTeamById(teamId);
      }

      // userStore의 teams를 teamStore의 userTeams로 설정
      this.teams = teamStore.userTeams;
      console.log('Teams:', this.teams);
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

      // 실제 API 호출 예제
      // try {
      //   const response = await axios.get(`http://localhost:8000/api/users`);
      //   this.userList = response.data.map(user => ({
      //     id: user.id,
      //     username: user.username,
      //     email: user.email
      //   }));
      //   console.log('AllUsers', this.userList);
      // } catch (error) {
      //   console.error('Failed to fetch users:', error);
      // }
    }
  }
});
