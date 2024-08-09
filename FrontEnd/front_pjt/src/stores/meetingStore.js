import { defineStore } from 'pinia'
import axiosInstance from '@/axios'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [
      {
        "meeting_id": 1,
        "team_id": 10,
        "name": "과거 회의",
        "start_at": "2024-07-17T09:00:00Z",
        "end_at": "2024-07-17T11:00:00Z",
        "details": "회의의 내용은 다음과 같습니다.",
        "content": "아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...",
        "created_at": "2024-07-15 9:00:00",
        "updated_at": "2024-07-16 9:00:00",
        "status": "OUT"
      },
      {
        "meeting_id": 2,
        "team_id": 10,
        "name": "오늘 회의",
        "start_at": "2024-08-09T11:00:00Z",
        "end_at": "2024-08-17T13:00:00Z",
        "details": "회의의 내용은 다음과 같습니다.",
        "content": "아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...",
        "created_at": "2024-07-15 9:00:00",
        "updated_at": "2024-07-16 9:00:00",
        "status": "IN"
      },
      {
        "meeting_id": 3,
        "team_id": 10,
        "name": "미래 회의",
        "start_at": "2024-09-07T13:00:00Z",
        "end_at": "2024-09-17T15:00:00Z",
        "details": "회의의 내용은 다음과 같습니다.",
        "content": "아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...",
        "created_at": "2024-07-15 9:00:00",
        "updated_at": "2024-07-16 9:00:00",
        "status": "OUT"
      }
    ],
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
      const today = new Date(); // 현재 시간을 포함한 오늘 날짜
      const startOfDay = new Date(today.setHours(0, 0, 0, 0)); // 자정으로 설정된 오늘의 시작
      const endOfDay = new Date(today.setHours(23, 59, 59, 999)); // 오늘의 끝
    
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
    }
  }
});
