import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useMeetingStore } from './meetingStore';
import { useErrorStore } from './errorStore'; // Import the errorStore
import axiosInstance from '@/axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [],
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
      this.clearTeamUsers();
      const meetingStore = useMeetingStore();
      
      try {
        const response = await axiosInstance.get(`api/teams/${teamId}`);
        const teamData = response.data.result;
        this.teamInfo = teamData
        this.teamUserList = teamData.userList
        const teamExists = this.teams.some(team => team.id == teamId);
        if (!teamExists) {
          this.teams.push({
            id: teamId,
            ...teamData
          });
        }
      } catch (error) {
        console.error(`Failed to fetch team ${teamId}:`, error);
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
        }else {
          const teamId = response.data.result;
          await this.fetchTeamById(teamId)
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

      const errorStore = useErrorStore(); 
      const userStore = useUserStore();
      let token = userStore.accessToken; // 사용자의 액세스 토큰
    
      try {
        const team = this.teams.find(team => team.id == teamId);
        console.log('Owner ID:', team.ownerId);
        console.log('User ID:', userStore.userId);
    
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
            'Authorization': `Bearer ${token}` // 토큰을 요청 헤더에 포함
          }
        });
    
        if (response.status === 403) {
          // 만약 403 Forbidden 오류가 발생시, 토큰 갱신 후 다시 시도
          console.log('Attempting to refresh token...');
          await userStore.refreshToken(); // 새 토큰으로 업뎃
          token = userStore.accessToken;
          response = await axiosInstance.delete(`api/teams/${teamId}`, {
            headers: {
              'Authorization': `Bearer ${token}` // 갱신된 토큰으로 재시도
            }
          });
        }
    
        if (response.data.isSuccess) {
          this.teams = this.teams.filter(team => team.id !== teamId);
        } else {
          errorStore.showError(`Failed to delete team: ${response.data.message}`);
        }
    
        return response.data;
      } catch (error) {
        if (error.response && error.response.status === 403) {
          errorStore.showError('Forbidden: You do not have permission to delete this team.');
        } else {
          errorStore.showError(`Failed to delete team ${teamId}: ${error.message}`);
        }
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
    getTeamById: (state) => (id) => state.teams.find(team => team.id === id),

     // 팀별로 이전/오늘/다음 미팅 시간 계산
    prevMeetingHoursByTeam: () => (teamId) => {
      const meetingStore = useMeetingStore();
      return meetingStore.prevMeetingHoursByTeam(teamId);
    },
    todayMeetingHoursByTeam: () => (teamId) => {
      const meetingStore = useMeetingStore();
      return meetingStore.todayMeetingHoursByTeam(teamId);
    },
    nextMeetingHoursByTeam: () => (teamId) => {
      const meetingStore = useMeetingStore();
      return meetingStore.nextMeetingHoursByTeam(teamId);
    },
    totalParticipantsByTeam: () => (teamId) => {
      const meetingStore = useMeetingStore();
      return meetingStore.totalParticipantsByTeam(teamId);
    },
  },
  persist: {
    key: 'teamStore',
    storage: sessionStorage,
  },
});