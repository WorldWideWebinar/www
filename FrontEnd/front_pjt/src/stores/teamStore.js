// import { defineStore } from 'pinia';

// export const useTeamStore = defineStore('team', {
//   state: () => ({
//     teams: [
//       { id: 1, teamName: 'R&D', ownerId: 2, icon: '🚀', userList: [1, 3], meetingList: [1, 2] },
//       { id: 2, teamName: 'Dev', ownerId: 1, icon: '💻', userList: [1, 3], meetingList: [4, 5] },
//       { id: 3, teamName: 'Pur', ownerId: 1, icon: '💼', userList: [1, 3], meetingList: [3, 6] },
//       { id: 4, teamName: 'Sales', ownerId: 1, icon: '📈', userList: [1, 3], meetingList: [7, 8] },
//     ],
//     isOwner: false,
//   }),
//   actions: {
//     async fetchTeamById(teamId) {
//       const teamData = this.teams.find(team => team.id === teamId);

//       if (teamData) {
//         return teamData;
//       } else {
//         console.error(`Team ${teamId} not found in static data`);
//         return null;
//       }
//     },
//     checkIfUserIsOwner(userId, teamName) {
//       const team = this.teams.find(t => t.teamName === teamName);
//       this.isOwner = team && team.ownerId === userId;
//     },
//     addTeam(team) {
//       this.teams.push(team);
//     }
//   },
//   getters: {
//     getTeamById: (state) => (id) => {
//       return state.teams.find(team => team.id === id);
//     },
//     getUserTeamsByHostId: (state) => (hostId) => {
//       return state.teams.filter(team => team.ownerId === hostId);
//     }
//   }
// });

import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:5000/api/',
});

api.interceptors.request.use(
  config => {
    const userStore = useUserStore();
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

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
    },
    async createTeam(teamName, ownerId, userList) {
      try {
        const response = await api.post('teams', { teamName, ownerId, userList });
        if (response.data.isSuccess) {
          const teamId = response.data.result.teamId;
          this.teams.push({ id: teamId, teamName, ownerId, userList, icon: '🆕', meetingList: [] });
        } else {
          console.error('Failed to create team:', response.data.message);
        }
      } catch (error) {
        console.error('Error creating team:', error);
      }
    },
    addMembertoTeam(userId, teamId) {
      const team = this.teams.find(team => team.id === teamId); // teamid 에 맞는 team을 불러오고 있으면 넣고 없으면 안 넣고
      if (team) {
        if (!team.userList.includes(userId)) { 
          team.userList.push(userId);
          console.log('성공');
          console.log(`팀 ${teamId}의 userList:`, JSON.stringify(team.userList)); //
        } else {
          console.log(`User ${userId} is already a member of team ${teamId}`);
        }
      } else {
        console.error(`Team ${teamId} not found`);
      }
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