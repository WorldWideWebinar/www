import { defineStore } from 'pinia';
import dayjs from 'dayjs';

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
  }),
  actions: {
    async fetchAllMeetingsByUser(userId) {
      // 가상의 데이터 설정
      this.meetings = [
        {
          id: 1,
          title: 'Meeting 1',
          date: dayjs().format('YYYY-MM-DD'),
          host: 'Host 1',
          participants: ['Participant 1', 'Participant 2'],
          teamId: 1 // R&D
        },
        {
          id: 2,
          title: 'Meeting 2',
          date: dayjs().add(1, 'day').format('YYYY-MM-DD'),
          host: 'Host 2',
          participants: ['Participant 3', 'Participant 4'],
          teamId: 2 // Development
        },
        {
          id: 3,
          title: 'Meeting 3',
          date: dayjs().add(2, 'day').format('YYYY-MM-DD'),
          host: 'Host 3',
          participants: ['Participant 5', 'Participant 6'],
          teamId: 3 // Purchase
        },
        {
          id: 4,
          title: 'Meeting 4',
          date: dayjs().add(3, 'day').format('YYYY-MM-DD'),
          host: 'Host 4',
          participants: ['Participant 7', 'Participant 8'],
          teamId: 4 // Sales
        }
      ];
      console.log('Meetings fetched:', this.meetings); // 디버깅용
    },
    addMeeting(meeting) {
      this.meetings.push(meeting);
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.teamId === teamId);
    },
  }
});

