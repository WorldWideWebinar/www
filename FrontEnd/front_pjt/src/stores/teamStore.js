import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useMeetingStore } from './meetingStore';
import { useErrorStore } from './errorStore'; // Import the errorStore
import axiosInstance from '@/axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [
    ],
    teamInfo : null,
    isOwner: false,
    teamUserList: [],
    teamUserInfo: [],
  }),
  actions: {
    clearTeams() {
      this.teams = [];
    }
    ,
    clearTeamUsers() {
      this.teamUserList = [];
      this.teamUserInfo = [];
    },
    async fetchTeamById(teamId) {
      this.clearTeamUsers()
      const meetingStore = useMeetingStore(); // Access the meeting store
      const errorStore = useErrorStore(); // Access the error store
      
      try {
        // 초기화 로직
        const response = await axiosInstance.get(`api/teams/${teamId}`);
        const teamData = response.data.result;
        this.teamInfo = teamData
        this.teamUserList = teamData.userList
        const teamExists = this.teams.some(team => team.id === teamId);
        if (!teamExists) {
          this.teams.push({
            id: teamId, // 추가된 ID 필드
            ...teamData
          });
          
          return teamData;
        }
      } catch (error) {
        errorStore.showError(`Failed to fetch team ${teamId}: ${error.message}`);
        return null;
      }
    },

    async fetchTeamUsers() {
      const errorStore = useErrorStore();
      try {
        // this.clearTeamUsers();
        
        const users = await Promise.all(this.teamUserList.map(async (userId) => {
          try {
            const response = await axiosInstance.get(`api/users/${userId}`);
            return response.data.result;
          } catch (error) {
            errorStore.showError(`Failed to fetch user info for user ${userId}`);
            return null;
          }
        }));
        this.teamUserInfo = users.filter(user => user !== null);
      } catch (error) {
        errorStore.showError('Failed to fetch team users');
      }
    },
    checkIfUserIsOwner(userId, teamName) {
      const team = this.teams.find(t => t.teamName === teamName);
      this.isOwner = team && team.ownerId === userId;
    },
    addTeam(team) {
      this.teams.push(team);
    },
    
    async createTeam(teamName, ownerId, emoji, userList) {
      const userStore = useUserStore();
      const errorStore = useErrorStore(); // Access the error store
      const currentUserId = userStore.id;
      userList.push(currentUserId);
      try {
        const response = await axiosInstance.post('api/teams', { teamName, ownerId, emoji, userList });
        if (!response.data.success) {
          errorStore.showError(`Failed to create team: ${response.data.message}`);
        }
      } catch (error) {
        errorStore.showError(`Error creating team: ${error.message}`);
      }
    },

    async editTeam(teamId, teamName, ownerId, emoji, userList,) {
      const errorStore = useErrorStore()
      try {
        const response = await axiosInstance.put(`api/teams/${teamId}`, { teamName, ownerId, emoji, userList})
        if (!response.data.success) {
          errorStore.showError('error')
        }
      } catch (error) {
        errorStore.showError(`Error editing team info: ${error.message}`)
      }
    },

    async deleteTeam(teamId) {
      const errorStore = useErrorStore(); // Access the error store
      try {
        const response = await axiosInstance.delete(`api/teams/${teamId}`);
        if (response.data.isSuccess) {
          this.teams = this.teams.filter(team => team.id !== teamId);
        } else {
          errorStore.showError(`Failed to delete team: ${response.data.message}`);
        }
        return response.data;
      } catch (error) {
        errorStore.showError(`Failed to delete team ${teamId}: ${error.message}`);
        return { isSuccess: false, message: error.message };
      }
    },

    async leaveTeam(teamId) {
      const userStore = useUserStore();
      const errorStore = useErrorStore(); // Access the error store
      const userId = userStore.userId;

      try {
        const response = await axiosInstance.put(`teams/${teamId}/${userId}`);
        if (response.data.isSuccess) {
          const team = this.teams.find(team => team.id === teamId);
          if (team) {
            team.userList = team.userList.filter(user => user !== userId);
          }
        } else {
          errorStore.showError(`Failed to remove user from team: ${response.data.message}`);
        }
        return response.data;
      } catch (error) {
        errorStore.showError(`Failed to remove user ${userId} from team ${teamId}: ${error.message}`);
        return { isSuccess: false, message: error.message };
      }
    },
    
    addMembertoTeam(userId, teamId) {
      const team = this.teams.find(team => team.id === teamId); // teamid 에 맞는 team을 불러오고 있으면 넣고 없으면 안 넣고
      if (team) {
        if (!team.userList.includes(userId)) { 
          team.userList.push(userId);
        }
      } else {
        const errorStore = useErrorStore(); // Access the error store
        errorStore.showError(`Team ${teamId} not found`);
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
  }, 
  persist: {
    key: 'teamStore',
    storage: sessionStorage,
  },
});