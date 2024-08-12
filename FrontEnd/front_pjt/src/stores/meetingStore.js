import { defineStore } from 'pinia'
import { useErrorStore } from './errorStore';
import axiosInstance from '@/axios'

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
          this.meetings.push(meeting);
          this.groupMeetings();
        } else {
          console.error('Failed to add meeting:', response.data.message);
        }
      } catch (error) {
        console.error('Error adding meeting:', error);
      }
    },
    async fetchMeetingById(meetingId) {
      try {
        const response = await axiosInstance.get(`api/meetings/${meetingId}`)
        const meeting = response.data.result
        if (!this.meetings.find((m) => m.id == meetingId)) {
          console.log("Meeting fetched", response.data.result)
          this.meetings.push(meeting)
        }
      } catch (error) {
        console.error(`Failed to fetch meeting ${meetingId}:`, error);
      }
    },

     
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id === teamId);
    },
    prevMeetingHoursByTeam: (state) => (teamId) => {
      return state.groupedMeetings.PREV
        .filter(meeting => meeting.team_id == teamId)
        .reduce((total, meeting) => {
          const start = new Date(meeting.start_at);
          const end = new Date(meeting.end_at);
          return total + (end - start) / (1000 * 60 * 60);
        }, 0);
    },
    todayMeetingHours(state) {
      return state.groupedMeetings.TODAY.reduce((total, meeting) => {
        const start = new Date(meeting.start_at);
        const end = new Date(meeting.end_at);
        return total + (end - start) / (1000 * 60 * 60);
      }, 0);
    },
    nextMeetingHours(state) {
      return state.groupedMeetings.NEXT.reduce((total, meeting) => {
        const start = new Date(meeting.start_at);
        const end = new Date(meeting.end_at);
        return total + (end - start) / (1000 * 60 * 60);
      }, 0);
    }
  }
});
