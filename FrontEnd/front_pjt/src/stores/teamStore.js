import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import axiosInstance from '@/axios';

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
      try {
        const response = await axiosInstance.get(`api/teams/${teamId}`);
        return response.data;
      } catch (error) {
        console.error(`Failed to fetch team ${teamId}:`, error);
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
        const response = await axiosInstance.post('api/teams', { teamName, ownerId, userList });
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

    async deleteTeam(teamId) {
      try {
        const response = await axiosInstance.delete(`api/teams/${teamId}`);
        if (response.data.isSuccess) {
          this.teams = this.teams.filter(team => team.id !== teamId);
        } else {
          console.error('Failed to delete team:', response.data.message);
        }
        return response.data;
      } catch (error) {
        console.error(`Failed to delete team ${teamId}:`, error);
        return { isSuccess: false, message: error.message };
      }
    },

    async leaveTeam(teamId) {
      try {
        const userStore = useUserStore();
        const userId = userStore.userId;

        const response = await axiosInstance.put(`teams/${teamId}/${userId}`);
        if (response.data.isSuccess) {
          const team = this.teams.find(team => team.id === teamId);
          if (team) {
            team.userList = team.userList.filter(user => user !== userId);
          }
        } else {
          console.error('Failed to remove user from team:', response.data.message);
        }
        return response.data;
      } catch (error) {
        console.error(`Failed to remove user ${userId} from team ${teamId}:`, error);
        return { isSuccess: false, message: error.message };
      }
    },
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
