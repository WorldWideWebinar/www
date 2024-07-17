import { defineStore } from 'pinia';
import { useTeamStore } from './teamStore';
import { useMeetingStore } from './meetingStore';

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

      this.teams = teamStore.teams;
      this.meetings = meetingStore.meetings;
    },
  }
});