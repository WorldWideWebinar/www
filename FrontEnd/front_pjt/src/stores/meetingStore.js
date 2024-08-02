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
    async fetchMeetingsByIds(meetingIds) {
      try {
        for (const meetingId of meetingIds) {
          await this.fetchMeetingById(meetingId)
        }
      } catch (error) {
        console.error('Failed to fetch meetings:', error)
      }
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter((meeting) => meeting.team_id === teamId)
    }
  }
})