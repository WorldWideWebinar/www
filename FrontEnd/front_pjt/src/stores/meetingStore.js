import { defineStore } from 'pinia'
import axiosInstance from '@/axios'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: []
  }),
  actions: {
    clearMeetings() {
      this.meetings = [];
    },
    async addMeeting(meeting) {
      try {
        const response = await axiosInstance.post('api/meetings', meeting)
        if (response.data.success) {
          
          this.meetings.push(meeting)
        } else {
          console.error('Failed to add meeting:', response.data.message)
        }
      } catch (error) {
        console.error('Error adding meeting:', error)
      }
    },
    async fetchMeetingById(meetingId) {
      try {
        const response = await axiosInstance.get(`api/meetings/${meetingId}`)
        const meeting = response.data.result
        if (!this.meetings.find((m) => m.id === meetingId)) {
          console.log("Meeting fetched",response.data.result)
          this.meetings.push(meeting)
        }
      } catch (error) {
        console.error(`Failed to fetch meeting ${meetingId}:`, error)
      }
    },

    async fetchMeetingsByIds(meetingList) {
      try {
        for (const meetingId of meetingList) {
          await this.fetchMeetingById(meetingId)
        }
      } catch (error) {
        console.error('Failed to fetch meetings:', error)
      }
    },

    async fetchMeetings(teamId, prev = false, next = false) {
      try {
        const today = new Date().toISOString(); // 현재 날짜와 시간을 ISO 형식으로 변환

        const params = {
          today: today,
          prev: prev,
          next: next,
          teamId: teamId
        };

        const response = await axiosInstance.get('/api/meetings', { params });
        const meetings = response.data.result;

        meetings.forEach((meeting) => {
          if (!this.meetings.find((m) => m.id === meeting.id)) {
            console.log("Meeting fetched", meeting);
            this.meetings.push(meeting);
          }
        });

      } catch (error) {
        console.error('Failed to fetch meetings:', error);
      }
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter((meeting) => meeting.team_id === teamId)
    }
  }
})