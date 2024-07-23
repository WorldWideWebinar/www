import { defineStore } from 'pinia';
import dayjs from 'dayjs';

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [],
  }),
  actions: {
    async fetchMeetingsByIds(meetingIds) {
      // 여기에 실제 API 호출을 추가하여 회의 데이터를 가져올 수 있습니다.
      // 예제에서는 임시 데이터를 사용합니다.
      const allMeetings = [
        {
          meeting_id: 1,
          team_id: 1,
          name: '프로젝트 아이디어 회의',
          start_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '아이디어 1: ..., 아이디어 2: ..., 아이디어 3: ...',
          created_at: dayjs().subtract(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 2,
          team_id: 1,
          name: '주간 개발 미팅',
          start_at: dayjs().add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(1, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '주간 개발 현황 논의',
          created_at: dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 3,
          team_id: 2,
          name: '구매 전략 회의',
          start_at: dayjs().add(2, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(2, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '구매 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 4,
          team_id: 2,
          name: '영업 전략 회의',
          start_at: dayjs().add(3, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(3, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '영업 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        // 추가 회의 데이터
        {
          meeting_id: 5,
          team_id: 3,
          name: '마케팅 전략 회의',
          start_at: dayjs().add(4, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(4, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '마케팅 전략 수립',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 6,
          team_id: 3,
          name: '제품 출시 회의',
          start_at: dayjs().add(5, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(5, 'day').add(2, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '제품 출시 준비',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 7,
          team_id: 4,
          name: '고객 피드백 회의',
          start_at: dayjs().add(6, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(6, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '고객 피드백 수집 및 분석',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        },
        {
          meeting_id: 8,
          team_id: 4,
          name: '내부 회고 회의',
          start_at: dayjs().add(7, 'day').format('YYYY-MM-DD HH:mm:ss'),
          end_at: dayjs().add(7, 'day').add(1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
          content: '내부 회고 및 개선 사항 논의',
          created_at: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          updated_at: dayjs().format('YYYY-MM-DD HH:mm:ss')
        }
      ];

      this.meetings = allMeetings.filter(meeting => meetingIds.includes(meeting.meeting_id));
      console.log('Meetings fetched (static data):', this.meetings);
    },

    addMeeting(meeting) {
      this.meetings.push(meeting);
    }
  },
  getters: {
    getMeetingsByTeamId: (state) => (teamId) => {
      return state.meetings.filter(meeting => meeting.team_id === teamId);
    },
  }
});
