import { defineStore } from 'pinia'
import { useErrorStore } from './errorStore';
import axiosInstance from '@/axios'
import { useTeamStore } from '@/stores/teamStore.js'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],

  }),
  actions: {
    clearMeetings() {
      this.meetings = [];
    },
    async addMeeting(meeting) {
      try {
        const response = await axiosInstance.post('api/meetings', meeting);
        if (response.data.success) {
          // await useTeamStore().fetchTeamById()
          // this.meetings.push(meeting);
          // this.groupMeetings();
          return true;
        } else {
          console.error('Failed to add meeting:', response.data.message);
          return false;
        }
      } catch (error) {
        console.error('Error adding meeting:', error);
        return false
      }
    },
    async fetchMeetingById(meetingId) {
      try {
        const response = await axiosInstance.get(`api/meetings/${meetingId}`);
        const meeting = response.data.result;
        if (!this.meetings.find((m) => m.id === meetingId)) {
          this.meetings.push(meeting);
          this.groupMeetings();
        }
      } catch (error) {
        console.error(`Failed to fetch meeting ${meetingId}:`, error);
      }
    },
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id == teamId);
    },
  }
});
