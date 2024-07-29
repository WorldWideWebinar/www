import { defineStore } from 'pinia';
import axiosInstance from '@/axios';

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
  }),
  actions: {
    async fetchMeetingsByIds(meetingIds) {
      try {
        const response = await axiosInstance.get('api/meetings', {
          params: { ids: meetingIds.join(',') }
        });
        this.meetings = response.data.filter(meeting => meetingIds.includes(meeting.meeting_id));
      } catch (error) {
        console.error('Failed to fetch meetings:', error);
      }
    },

    async addMeeting(meeting) {
      try {
        const response = await axiosInstance.post('meetings', meeting);
        if (response.data.isSuccess) {
          this.meetings.push(response.data.meeting);
        } else {
          console.error('Failed to add meeting:', response.data.message);
        }
      } catch (error) {
        console.error('Error adding meeting:', error);
      }
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id === teamId);
    },
  }
});

