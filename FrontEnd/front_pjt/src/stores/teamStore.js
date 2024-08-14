// teamStore.js
import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useMeetingStore } from './meetingStore';
import { useErrorStore } from './errorStore';
import axiosInstance from '@/axios';
import router from '@/router/index.js'

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

        if (response.data.success) {
          this.teams = this.teams.filter(team => team.id != teamId);
          router.push({name : "HomeView"});
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
        const response = await axiosInstance.put(`api/teams/${teamId}/${userId}`);
        if (response.data.success) {
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

    async addMemberToTeam(invitedUser, teamId) {
      try {
        if (!this.teamInfo) {
          throw new Error('Team information is not loaded.');
        }
    
        // Extract existing user IDs from teamUserInfo
        const existingUserIds = this.teamUserInfo.map(user => user.id);
    
        // Check if the invited user's id is already in the list
        if (!existingUserIds.includes(invitedUser.id)) {
          // Add the invited user's id to the list
          existingUserIds.push(invitedUser.id);
        } 
        const payload = {
          teamName: this.teamInfo.teamName,
          ownerId: this.teamInfo.ownerId,
          emoji: this.teamInfo.emoji,
          userList: existingUserIds,
        };
    
        // Send the updated team info via PUT request
        await axiosInstance.put(`/api/teams/${teamId}`, payload);

        const errorStore = useErrorStore();
        errorStore.showError('Invite successful!')
      } catch (error) {
        console.error('Failed to add member to team:', error);
        throw error;
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
      const sortMeetingsByDateTime = (meetings) => {
        return meetings.sort((a, b) => {
          const startDateA = new Date(a.start_at);
          const startDateB = new Date(b.start_at);
          const endDateA = new Date(a.end_at);
          const endDateB = new Date(b.end_at);
    
          // 시작 날짜 기준 오름차순
          if (startDateA > startDateB) return 1;
          if (startDateA < startDateB) return -1;
    
          // 시작 시간이 같은 경우, 시작 시간 기준 오름차순
          if (startDateA.getTime() > startDateB.getTime()) return 1;
          if (startDateA.getTime() < startDateB.getTime()) return -1;
    
          // 시작 날짜와 시간이 같은 경우, 종료 날짜 기준 오름차순
          if (endDateA > endDateB) return 1;
          if (endDateA < endDateB) return -1;
    
          // 종료 날짜와 시간이 같은 경우, 종료 시간 기준 오름차순
          if (endDateA.getTime() > endDateB.getTime()) return 1;
          if (endDateA.getTime() < endDateB.getTime()) return -1;
    
          return 0;
        });
      };
    
      if (prev) {
        this.groupedMeetings.PREV = sortMeetingsByDateTime(meetings);
      }
      if (next) {
        this.groupedMeetings.NEXT = sortMeetingsByDateTime(meetings);
      }
      if (!prev && !next) {
        this.groupedMeetings.TODAY = sortMeetingsByDateTime(meetings);
      }
    },
    
    async loadData(teamId) {
      try {
        await this.fetchTeamById(teamId);
        await Promise.all([
          this.fetchTeamUsers(),
          this.fetchMeetings(teamId, false, false), // TODAY meetings
          this.fetchMeetings(teamId, false, true),  // NEXT meetings
          this.fetchMeetings(teamId, true, false)   // PREV meetings
        ]);
      } catch (error) {
        const errorStore = useErrorStore();
        errorStore.showError(`Failed to load team data: ${error.message}`);
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
