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
    
        console.log('서버로부터 받은 회의 데이터:', newMeetings);
    
        const existingMeetingIds = new Set(this.meetings.map(meeting => meeting.id));
        const filteredMeetings = newMeetings.filter(meeting => !existingMeetingIds.has(meeting.id));
    
        this.meetings.push(...filteredMeetings);
    
        this.groupMeetings();
        console.log('Grouped Meetings after fetching:', this.groupedMeetings);
    
      } catch (error) {
        console.error('Failed to fetch meetings:', error);
      }
    },
    groupMeetings() {
      const today = new Date();
      const startOfDay = new Date(today.setHours(0, 0, 0, 0));
      const endOfDay = new Date(today.setHours(23, 59, 59, 999));

      this.groupedMeetings.PREV = this.meetings.filter(meeting => {
        const endDate = new Date(meeting.end_at);
        return endDate < startOfDay;
      });

      this.groupedMeetings.TODAY = this.meetings.filter(meeting => {
        const startDate = new Date(meeting.start_at);
        return startDate >= startOfDay && startDate <= endOfDay;
      });

      this.groupedMeetings.NEXT = this.meetings.filter(meeting => {
        const startDate = new Date(meeting.start_at);
        return startDate > endOfDay;
      });
    
      console.log('Grouped Meetings:', this.groupedMeetings);
    }
     
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id === teamId);
    },
    
    // 팀별 이전 미팅 시간 합계 계산
    prevMeetingHoursByTeam: (state) => (teamId) => {
      return state.groupedMeetings.PREV
        .filter(meeting => meeting.team_id == teamId)
        .reduce((total, meeting) => {
          const start = new Date(meeting.start_at);
          const end = new Date(meeting.end_at);
          return total + (end - start) / (1000 * 60 * 60); // 시간을 시간 단위로 계산
        }, 0);
    },
    
    // 팀별 오늘 미팅 시간 합계 계산
    todayMeetingHoursByTeam: (state) => (teamId) => {
      return state.groupedMeetings.TODAY
        .filter(meeting => meeting.team_id == teamId)
        .reduce((total, meeting) => {
          const start = new Date(meeting.start_at);
          const end = new Date(meeting.end_at);
          return total + (end - start) / (1000 * 60 * 60);
        }, 0);
    },
    
    // 팀별 다음 미팅 시간 합계 계산
    nextMeetingHoursByTeam: (state) => (teamId) => {
      return state.groupedMeetings.NEXT
        .filter(meeting => meeting.team_id == teamId)
        .reduce((total, meeting) => {
          const start = new Date(meeting.start_at);
          const end = new Date(meeting.end_at);
          return total + (end - start) / (1000 * 60 * 60);
        }, 0);
    },
    
    // 팀별 전체 참가자 수 계산
    totalParticipantsByTeam: (state) => (teamId) => {
      return state.meetings
        .filter(meeting => meeting.team_id == teamId)
        .reduce((total, meeting) => {
          return total + (meeting.participants?.length || 0);
        }, 0);
    },
    meetingDuration: (state) => (meetingId) => {
      const meeting = state.meetings.find(meeting => meeting.id === meetingId);
      if (!meeting) return 0;
      const start = new Date(meeting.start_at);
      const end = new Date(meeting.end_at);
      return (end - start) / (1000 * 60 * 60); // 시간 단위로 계산
    },
    meetingParticipantsCount: (state) => (meetingId) => {
      const meeting = state.meetings.find(meeting => meeting.id === meetingId);
      return meeting ? (meeting.participants?.length || 0) : 0;
    },
  }
});
