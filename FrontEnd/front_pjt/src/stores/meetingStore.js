import { defineStore } from 'pinia'
import { useErrorStore } from './errorStore';
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
        if (!this.meetings.find((m) => m.id == meetingId)) {
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

    async fetchMeetings(teamId, prev = false, next = false) {
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
    
        console.log('서버로부터 받은 회의 데이터:', newMeetings);
    
        const existingMeetingIds = new Set(this.meetings.map(meeting => meeting.id));
        const filteredMeetings = newMeetings.filter(meeting => !existingMeetingIds.has(meeting.id));
    
        this.meetings.push(...filteredMeetings);

        this.groupMeetings(prev, next); // 새로 가져온 미팅을 그룹화
      } catch (error) {
        if (error.response && error.response.status !== 404) {
          console.error('Failed to fetch meetings:', error)
        }
      }
    },

    groupMeetings(prev, next) {
      if (prev) {
        this.groupedMeetings.PREV = [...this.meetings];
        this.groupedMeetings.TODAY = [];
        this.groupedMeetings.NEXT = [];
      } else if (next) {
        this.groupedMeetings.PREV = [];
        this.groupedMeetings.TODAY = [];
        this.groupedMeetings.NEXT = [...this.meetings];
      } else {
        this.groupedMeetings.PREV = [];
        this.groupedMeetings.TODAY = [...this.meetings];
        this.groupedMeetings.NEXT = [];
      }
    },

    async deleteMeeting(meetingId) {
      const errorStore = useErrorStore(); // Access the error store
      try {
        const response = await axiosInstance.delete(`api/meetings/${meetingId}`);
        if (response.data.isSuccess) {
          this.meetings = this.meetings.filter(meeting => meeting.id !== meetingId);
        } else {
          errorStore.showError(`Failed to delete team: ${response.data.message}`);
        }
        return response.data;
      } catch (error) {
        errorStore.showError(`Failed to delete team ${meetingId}: ${error.message}`);
        return { isSuccess: false, message: error.message };
      }
    },
     
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id === teamId);
    },
    prevMeetingHours(state) {
      return state.groupedMeetings.PREV.reduce((total, meeting) => {
        const start = new Date(meeting.start_at);
        const end = new Date(meeting.end_at);
        return total + (end - start) / (1000 * 60 * 60); // 시간을 시간 단위로 계산
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
  },
  persist: {
    key: 'meetingStore',
    storage: sessionStorage,
  },
})
