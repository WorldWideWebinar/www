import { defineStore } from 'pinia';
// import axios from 'axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [
      { id: 1, teamName: 'R&D', ownerId: 1, icon: '🚀', userList: [1, 3] },
      { id: 2, teamName: 'Dev', ownerId: 1, icon: '💻', userList: [1, 3] },
      { id: 3, teamName: 'Pur', ownerId: 1, icon: '💼', userList: [1, 3] },
      { id: 4, teamName: 'Sales', ownerId: 1, icon: '📈', userList: [1, 3] },
    ],
    userTeams: [],
    isOwner: false,
  }),
  actions: {
    async fetchTeamById(teamId) {
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`http://localhost:8000/api/teams/${teamId}`);
      //   const teamData = response.data.data;

      //   // 이미 존재하는 팀인지 확인
      //   const existingTeam = this.teams.find(team => team.id === teamId);
      //   if (!existingTeam) {
      //     this.teams.push(teamData);
      //   }

      //   // userTeams에도 추가
      //   const userTeamExists = this.userTeams.find(team => team.id === teamId);
      //   if (!userTeamExists) {
      //     this.userTeams.push(teamData);
      //   }

      //   console.log(`Team ${teamId} fetched:`, teamData);
      // } catch (error) {
      //   console.error(`Failed to fetch team ${teamId}:`, error);
      // }

      // 임시 데이터 사용
      const teamData = this.teams.find(team => team.id === teamId);

      if (teamData) {
        // 이미 존재하는 팀인지 확인
        const existingTeam = this.teams.find(team => team.id === teamId);
        if (!existingTeam) {
          this.teams.push(teamData);
        }

        // userTeams에도 추가
        const userTeamExists = this.userTeams.find(team => team.id === teamId);
        if (!userTeamExists) {
          this.userTeams.push(teamData);
        }

        console.log(`Team ${teamId} fetched (static data):`, teamData);
      } else {
        console.error(`Team ${teamId} not found in static data`);
      }
    },
    checkIfUserIsOwner(userId, teamName) {
      const team = this.teams.find(t => t.teamName === teamName);
      this.isOwner = team && team.ownerId === userId;
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