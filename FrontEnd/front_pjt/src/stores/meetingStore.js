import { defineStore } from 'pinia'
import axiosInstance from '@/axios'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
    groupedMeetings: {
      PREV: [],
      TODAY: [],
      NEXT: []
    },
  }),
  actions: {
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
          console.log("Meeting fetched", response.data.result)
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

    async fetchMeetings(teamId, prev = 0, next = 0) {
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

        const existingMeetingIds = new Set(this.meetings.map(meeting => meeting.id));
        const filteredMeetings = newMeetings.filter(meeting => !existingMeetingIds.has(meeting.id));

        this.meetings.push(...filteredMeetings);

        this.groupMeetings();

      } catch (error) {
        console.error('Failed to fetch meetings:', error);
      }
    },
    groupMeetings() {
      const today = new Date().toISOString().split('T')[0];
      this.groupedMeetings.PREV = this.meetings.filter(meeting => meeting.end_at.split('T')[0] < today);
      this.groupedMeetings.TODAY = this.meetings.filter(meeting => meeting.start_at.split('T')[0] === today);
      this.groupedMeetings.NEXT = this.meetings.filter(meeting => meeting.start_at.split('T')[0] > today);
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter((meeting) => meeting.team_id === teamId)
    }
  }
})
