import { defineStore } from 'pinia';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [
      { id: 1, teamName: 'R&D', ownerId: 2, icon: '🚀', userList: [1, 3], meetingList: [1, 2] },
      { id: 2, teamName: 'Dev', ownerId: 1, icon: '💻', userList: [1, 3], meetingList: [4, 5] },
      { id: 3, teamName: 'Pur', ownerId: 1, icon: '💼', userList: [1, 3], meetingList: [3, 6] },
      { id: 4, teamName: 'Sales', ownerId: 1, icon: '📈', userList: [1, 3], meetingList: [7, 8] },
    ],
    isOwner: false,
  }),
  actions: {
    async fetchTeamById(teamId) {
      const teamData = this.teams.find(team => team.id === teamId);

      if (teamData) {
        console.log(`Team ${teamId} fetched (static data):`, teamData);
        return teamData;
      } else {
        console.error(`Team ${teamId} not found in static data`);
        return null;
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
      return state.teams.filter(team => team.ownerId === hostId);
    }
  }
});
