import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    teams: [],
    meetings: [],
  }),
  actions: {
    async fetchUserTeamsAndMeetings(userId) {
      this.userId = userId;

      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();

      await teamStore.fetchUserTeams(userId);
      await meetingStore.fetchAllMeetingsByUser(userId);

      this.teams = teamStore.userTeams; // userTeams로 변경
      this.meetings = meetingStore.userMeetings; // userMeetings로 변경
    },
    
    async fetchAllUsers() {

      try {
        const response = await axios.get(`http://localhost:8000/api/users`);
        this.userMeetings = response.data.map(meeting => ({
          ...meeting,

        }));
        console.log('AllUsers', this.userMeetings);
      } catch (error) {
        console.error('Failed to fetch user meetings:', error);
      }
    }
  }
});

