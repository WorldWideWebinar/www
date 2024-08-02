import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useMeetingStore } from './meetingStore';
import axiosInstance from '@/axios';

export const useTeamStore = defineStore('team', {
  state: () => ({
    teams: [],
    isOwner: false,
    teamUserList: [],
  }),
  actions: {
    clearTeams() {
      this.teams = [];
    },
    async fetchTeamById(teamId) {
      const meetingStore = useMeetingStore(); // Access the meeting store
      try {
        // 초기화 로직
        meetingStore.clearMeetings();

        const response = await axiosInstance.get(`api/teams/${teamId}`);
        const teamData = response.data.result;
        console.log('Teamdata', teamData);
        const teamExists = this.teams.some(team => team.id === teamId);
        if (!teamExists) {
          this.teams.push({
            id: teamId, // 추가된 ID 필드
            ...teamData
          });
        } else {
          console.log(`Team with id ${teamId} already exists in the store`);
        }

        // Fetch meetings for the team
        await meetingStore.fetchMeetingsByIds(teamData.meetingList);

        return teamData;

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
    
    async createTeam(teamName, ownerId, emoji, userList) {
      const userStore = useUserStore()
      const currentUserId = userStore.id;
      userList.push(currentUserId);
      try {
        const response = await axiosInstance.post('api/teams', { teamName, ownerId, emoji, userList });
        if (response.data.success) {
          console.log("TeamStore" ,this.teams)
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
  }, 
});
