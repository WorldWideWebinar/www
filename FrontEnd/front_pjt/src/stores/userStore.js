// import { defineStore } from 'pinia';
// import dayjs from 'dayjs';
// // import axios from 'axios'; // 백엔드 구축 후 주석 해제

// // const API_URL = 'https://your-backend-api.com';

// export const useUserStore = defineStore('user', {
//   state: () => ({
//     userId: 'sampleUserId',
//     sessions: [
//       { id: 1, name: 'R&D', displayName: 'R&D', icon: '🚀' },
//       { id: 2, name: 'Development', displayName: '개발', icon: '💻' },
//       { id: 3, name: 'Purchase', displayName: '구매', icon: '💼' },
//       { id: 4, name: 'Sales', displayName: '영업', icon: '📈' },
//     ],
//     meetings: [
//       {
//         id: 1,
//         title: 'Meeting 1',
//         date: dayjs().format('YYYY-MM-DD'),
//         host: 'Host 1',
//         participants: ['Participant 1', 'Participant 2'],
//         sessionId: 1 // R&D
//       },
//       {
//         id: 2,
//         title: 'Meeting 2',
//         date: dayjs().add(1, 'day').format('YYYY-MM-DD'),
//         host: 'Host 2',
//         participants: ['Participant 3', 'Participant 4'],
//         sessionId: 2 // Development
//       },
//       {
//         id: 3,
//         title: 'Meeting 3',
//         date: dayjs().add(2, 'day').format('YYYY-MM-DD'),
//         host: 'Host 3',
//         participants: ['Participant 5', 'Participant 6'],
//         sessionId: 3 // Purchase
//       },
//       {
//         id: 4,
//         title: 'Meeting 4',
//         date: dayjs().add(3, 'day').format('YYYY-MM-DD'),
//         host: 'Host 4',
//         participants: ['Participant 7', 'Participant 8'],
//         sessionId: 4 // Sales
//       },
//       {
//         id: 5,
//         title: 'Meeting 5',
//         date: dayjs().add(4, 'day').format('YYYY-MM-DD'),
//         host: 'Host 5',
//         participants: ['Participant 9', 'Participant 10'],
//         sessionId: 5 // Session 1
//       },
//       {
//         id: 6,
//         title: 'Meeting 6',
//         date: dayjs().add(5, 'day').format('YYYY-MM-DD'),
//         host: 'Host 6',
//         participants: ['Participant 11', 'Participant 12'],
//         sessionId: 6 // Session 2
//       }
//     ],
//   }),
//   actions: {
//     async fetchUserSessions(userId) {
//       this.userId = userId;
//       // 백엔드 구축 후 주석 해제
//       // try {
//       //   const response = await axios.get(`${API_URL}/users/${userId}/sessions`);
//       //   this.sessions = response.data;
//       //   await this.fetchAllMeetings();
//       // } catch (error) {
//       //   console.error('Failed to fetch user sessions:', error);
//       // }
//     },
//     async fetchAllMeetings() {
//       // 백엔드 구축 후 주석 해제
//       // try {
//       //   const meetingPromises = this.sessions.map(session => 
//       //     axios.get(`${API_URL}/sessions/${session.id}/meetings`)
//       //   );
//       //   const meetingsResponses = await Promise.all(meetingPromises);
//       //   this.meetings = meetingsResponses.flatMap(response => response.data);
//       // } catch (error) {
//       //   console.error('Failed to fetch meetings:', error);
//       // }
//     },
//     addMeeting(meeting) {
//       this.meetings.push(meeting);
//     }
//   }
// });

import { defineStore } from 'pinia';
import { useSessionStore } from './sessionStore';
import { useMeetingStore } from './meetingStore';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 'sampleUserId',
  }),
  actions: {
    async fetchUserSessionsAndMeetings(userId) {
      this.userId = userId;

      const sessionStore = useSessionStore();
      const meetingStore = useMeetingStore();

      await sessionStore.fetchUserSessions(userId);
      await meetingStore.fetchAllMeetingsByUser(userId);
    },
  }
});