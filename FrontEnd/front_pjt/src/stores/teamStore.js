import { defineStore } from 'pinia';
// import axios from 'axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [
      { id: 1, name: 'R&D', displayName: 'R&D', icon: '🚀', userList: [1, 3] },
      { id: 2, name: 'Development', displayName: '개발', icon: '💻', userList: [1, 3] },
      { id: 3, name: 'Purchase', displayName: '구매', icon: '💼', userList: [1, 3] },
      { id: 4, name: 'Sales', displayName: '영업', icon: '📈', userList: [1, 3] },
    ],
    userTeams: [],
  }),
  actions: {
    async fetchAllTeams() {
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`${API_URL}/teams`);
      //   this.teams = response.data;
      //   console.log('Teams fetched:', this.teams);
      // } catch (error) {
      //   console.error('Failed to fetch teams:', error);
      // }
      console.log('Using static team data:', this.teams);
    },

    async fetchUserTeams(userId) {
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`${API_URL}/users/${userId}/teams`);
      //   this.userTeams = response.data;
      //   console.log('User teams fetched:', this.userTeams);
      // } catch (error) {
      //   console.error('Failed to fetch user teams:', error);
      //   // API 호출이 실패할 경우 가상의 데이터를 사용
      //   this.userTeams = this.teams.filter(team => team.userList.includes(userId));
      // }
      // 임시 데이터 사용
      this.userTeams = this.teams.filter(team => team.userList.includes(userId));
      console.log('User teams fetched (static data):', this.userTeams);
    },

    addTeam(team) {
      this.teams.push(team);
    }
  },
  getters: {
    getTeamById: (state) => (id) => {
      return state.teams.find(team => team.id === id);
    },
    getUserTeamsByHostId: (state) => (hostId) => {
      return state.userTeams.filter(team => team.host === hostId);
    }
  }
});
