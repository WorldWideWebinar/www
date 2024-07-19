import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    teams: [],
    meetings: [],
    userList: [],
  }),
  actions: {
    async fetchUserTeamsAndMeetings(userId) {
      this.userId = userId;

      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();

      await teamStore.fetchUserTeams(userId);
      await meetingStore.fetchAllMeetingsByUser(userId);

      this.teams = teamStore.userTeams; // userTeams로 변경
      this.meetings = meetingStore.userMeetings; // userMeetings로 변경
    },
    
    async fetchAllUsers() {
      // 임의의 데이터를 사용하여 userList를 설정
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

