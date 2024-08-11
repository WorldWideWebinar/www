import { defineStore } from 'pinia'
import axiosInstance from '@/axios'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: []
  }),
  actions: {
    clearMeetings() {
      this.meetings = [];
<<<<<<< HEAD
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
=======
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

>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
    async fetchMeetingsByIds(meetingList) {
      try {
        for (const meetingId of meetingList) {
          await this.fetchMeetingById(meetingId)
        }
      } catch (error) {
        console.error('Failed to fetch meetings:', error)
      }
<<<<<<< HEAD
=======
    },

    async fetchMeetings(teamId, prev = 0, next = 0) {
      try {
        const today = new Date().toISOString(); // 현재 날짜와 시간을 ISO 형식으로 변환

        const params = {
          today: today,
          prev: prev,
          next: next,
          teamId: teamId
        };

        const response = await axiosInstance.get('/api/meetings', { params });
        const newMeetings = response.data.result;

        // 중복되지 않은 새로운 meetings를 추가
        const existingMeetingIds = new Set(this.meetings.map(meeting => meeting.id));
        const filteredMeetings = newMeetings.filter(meeting => !existingMeetingIds.has(meeting.id));

        this.meetings.push(...filteredMeetings);

      } catch (error) {
        console.error('Failed to fetch meetings:', error);
      }
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter((meeting) => meeting.team_id === teamId)
    }
  }
})