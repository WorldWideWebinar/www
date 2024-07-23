import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
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
      const meetingStore = useMeetingStore();
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`http://localhost:5000/api/users/${userId}`);
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
    async signup({ id, idCheck, name, email, password, language }) {
      try {
        // 아이디 중복 체크
        const duplicationResponse = await axios.get(`http://localhost:5000/api/users/duplication/${id}`);
        if (duplicationResponse.data.result.isAvailable) {
          // 회원가입 요청
          const signupResponse = await axios.post('http://localhost:5000/api/users', {
            id,
            idCheck,
            name,
            email,
            password,
            language
          });
          console.log('Signup successful:', signupResponse.data);
          return signupResponse.data;
        } else {
          console.log('ID is not available');
          return { isSuccess: false, message: 'ID is not available' };
        }
      } catch (error) {
        console.error('Failed to sign up:', error);
        return { isSuccess: false, message: error.message };
      }
    }
  }
});


