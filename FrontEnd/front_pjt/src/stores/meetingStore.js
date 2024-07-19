import { defineStore } from 'pinia';
import dayjs from 'dayjs';
// import axios from 'axios';

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
    userMeetings: [],
  }),
  actions: {
    async fetchAllMeetings() {
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get("http://localhost:8000/api/meetings/");
      //   this.meetings = response.data.map(meeting => ({
      //     ...meeting,
      //     date: dayjs(meeting.date).format('YYYY-MM-DD')
      //   }));
      //   console.log('Meetings fetched:', this.meetings);
      // } catch (error) {
      //   console.error('Failed to fetch meetings:', error);
      // }
      // 임시 데이터 사용
      this.meetings = [
        {
          meeting_id: 1,
          team_id: 10,
          name: '프로젝트 아이디어 회의',
          start_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...',
          created_at: dayjs().subtract(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 2,
          team_id: 20,
          name: '주간 개발 미팅',
          start_at: dayjs().add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(1, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '주간 개발 현황 논의',
          created_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 3,
          team_id: 30,
          name: '구매 전략 회의',
          start_at: dayjs().add(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '구매 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 4,
          team_id: 40,
          name: '영업 전략 회의',
          start_at: dayjs().add(3, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(3, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '영업 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        }
      ];
      console.log('Meetings fetched (static data):', this.meetings);
    },

    async fetchAllMeetingsByUser(userId) {
      // 주석 처리된 axios 호출
      // try {
      //   const response = await axios.get(`http://localhost:8000/api/meetings/user/${userId}`);
      //   this.userMeetings = response.data.map(meeting => ({
      //     ...meeting,
      //     date: dayjs(meeting.date).format('YYYY-MM-DD')
      //   }));
      //   console.log('User meetings fetched:', this.userMeetings);
      // } catch (error) {
      //   console.error('Failed to fetch user meetings:', error);
      //   // API 호출이 실패할 경우 가상의 데이터를 사용
      //   this.userMeetings = [
      //     {
      //       id: 1,
      //       title: 'Meeting 1',
      //       date: dayjs().format('YYYY-MM-DD'),
      //       host: 'Host 1',
      //       participants: ['Participant 1', 'Participant 2'],
      //       teamId: 1 // R&D
      //     },
      //     {
      //       id: 2,
      //       title: 'Meeting 2',
      //       date: dayjs().add(1, 'day').format('YYYY-MM-DD'),
      //       host: 'Host 2',
      //       participants: ['Participant 3', 'Participant 4'],
      //       teamId: 2 // Development
      //     },
      //     {
      //       id: 3,
      //       title: 'Meeting 3',
      //       date: dayjs().add(2, 'day').format('YYYY-MM-DD'),
      //       host: 'Host 3',
      //       participants: ['Participant 5', 'Participant 6'],
      //       teamId: 3 // Purchase
      //     },
      //     {
      //       id: 4,
      //       title: 'Meeting 4',
      //       date: dayjs().add(3, 'day').format('YYYY-MM-DD'),
      //       host: 'Host 4',
      //       participants: ['Participant 7', 'Participant 8'],
      //       teamId: 4 // Sales
      //     }
      //   ];
      //   console.log('Fallback user meetings fetched:', this.userMeetings); // 디버깅용
      // }
      // 임시 데이터 사용
      this.userMeetings = [
        {
          meeting_id: 1,
          team_id: 10,
          name: '프로젝트 아이디어 회의',
          start_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...',
          created_at: dayjs().subtract(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 2,
          team_id: 20,
          name: '주간 개발 미팅',
          start_at: dayjs().add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(1, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '주간 개발 현황 논의',
          created_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 3,
          team_id: 30,
          name: '구매 전략 회의',
          start_at: dayjs().add(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '구매 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 4,
          team_id: 40,
          name: '영업 전략 회의',
          start_at: dayjs().add(3, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(3, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '영업 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        }
      ];
      console.log('User meetings fetched (static data):', this.userMeetings);
    },

    addMeeting(meeting) {
      this.meetings.push(meeting);
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.teamId === teamId);
    },
    getUserMeetingsByTeamId: (state) => (teamId) => {
      return state.userMeetings.filter(meeting => meeting.teamId === teamId);
    }
  }
});
