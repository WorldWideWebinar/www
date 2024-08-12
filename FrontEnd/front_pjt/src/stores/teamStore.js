// teamStore.js
import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useMeetingStore } from './meetingStore';
import { useErrorStore } from './errorStore';
import axiosInstance from '@/axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [],
    teamInfo: null,
    isOwner: false,
    teamUserList: [],
    teamUserInfo: [],
    groupedMeetings: {
      PREV: [],
      TODAY: [],
      NEXT: []
    },
  }),
  actions: {
    clearTeams() {
      this.teams = [];
    },
    clearTeamUsers() {
      this.teamUserList = [];
      this.teamUserInfo = [];
    },
    clearTeamMeetings(){
      this.groupedMeetings = {
        PREV: [],
        TODAY: [],
        NEXT: []

      }
    },
    async fetchTeamById(teamId) {
      this.clearTeamUsers();
      const errorStore = useErrorStore();
      try {
        // 초기화 로직
        const response = await axiosInstance.get(`api/teams/${teamId}`);
        const teamData = response.data.result;
        this.teamInfo = teamData
        this.teamUserList = teamData.userList
        const teamExists = this.teams.some(team => team.id == teamId);
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
    async createTeam(teamName, ownerId, emoji, userList) {
      const userStore = useUserStore();
      const errorStore = useErrorStore();
      const currentUserId = userStore.id;
      userList.push(currentUserId);
      try {
        const response = await axiosInstance.post('api/teams', { teamName, ownerId, emoji, userList });
        if (!response.data.success) {
          errorStore.showError(`Failed to create team: ${response.data.message}`);
        }else {
          const teamId = response.data.result;
          await this.fetchTeamById(teamId)
        }
      } catch (error) {
        errorStore.showError(`Error creating team: ${error.message}`);
      }
    },

    async editTeam(teamId, teamName, ownerId, emoji, userList) {
      const errorStore = useErrorStore();
      try {
        const response = await axiosInstance.put(`api/teams/${teamId}`, { teamName, ownerId, emoji, userList });
        if (!response.data.success) {
          errorStore.showError('error');
        }
      } catch (error) {
        errorStore.showError(`Error editing team info: ${error.message}`);
      }
    },

    async deleteTeam(teamId) {
      const errorStore = useErrorStore();
      const userStore = useUserStore();
      let token = userStore.accessToken;

      try {
        const team = this.teams.find(team => team.id == teamId);

        if (!team) {
          errorStore.showError('Team not found');
          return { isSuccess: false, message: 'Team not found' };
        }

        if (team.ownerId != userStore.userId) {
          errorStore.showError('You do not have permission to delete this team');
          return { isSuccess: false, message: 'You do not have permission to delete this team' };
        }

        let response = await axiosInstance.delete(`api/teams/${teamId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.status === 403) {
          await userStore.refreshToken();
          token = userStore.accessToken;
          response = await axiosInstance.delete(`api/teams/${teamId}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
        }

        if (response.data.isSuccess) {
          this.teams = this.teams.filter(team => team.id != teamId);
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
      const errorStore = useErrorStore();
      const userId = userStore.userId;

      try {
        const response = await axiosInstance.put(`teams/${teamId}/${userId}`);
        if (response.data.isSuccess) {
          const team = this.teams.find(team => team.id == teamId);
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
      const team = this.teams.find(team => team.id == teamId);
      if (team) {
        if (!team.userList.includes(userId)) {
          team.userList.push(userId);
        }
      } else {
        const errorStore = useErrorStore();
        errorStore.showError(`Team ${teamId} not found`);
      }
    },

    async fetchMeetings(teamId, prev = false, next = false) {
      try {
        const today = new Date().toISOString();
        const params = {
          today: today,
          prev: prev,
          next: next,
          teamId: teamId
        };
        const response = await axiosInstance.get('/api/meetings', { params });
        const newMeetings = response.data.result;
        const meetingStore = useMeetingStore();

        meetingStore.meetings.push(newMeetings);
        this.groupMeetings(prev, next, newMeetings);
      } catch (error) {
        if (error.response && error.response.status !== 404) {
          console.error('Failed to fetch meetings:', error)
        }
      }
    },

    groupMeetings(prev, next, meetings) {
      if (prev) {
        this.groupedMeetings.PREV = [...meetings];
      }
      if (next) {
        this.groupedMeetings.NEXT = [...meetings];
      }
      if(!prev && !next){
        this.groupedMeetings.TODAY = [...meetings];
      }
    },
  },
  getters: {
    getTeamById: (state) => (id) => state.teams.find(team => team.id === id),
  },
  persist: {
    key: 'teamStore',
    storage: sessionStorage,
  },
});
