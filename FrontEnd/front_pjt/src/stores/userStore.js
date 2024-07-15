import { defineStore } from 'pinia';
import dayjs from 'dayjs';
// import axios from 'axios'; // 백엔드 구축 후 주석 해제

// const API_URL = 'https://your-backend-api.com';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 'sampleUserId',
    sessions: [
      { id: 1, name: 'Session 1' },
      { id: 2, name: 'Session 2' }
    ],
    meetings: [
      {
        id: 1,
        title: 'Meeting 1',
        date: dayjs().format('YYYY-MM-DD'),
        host: 'Host 1',
        participants: ['Participant 1', 'Participant 2']
      },
      {
        id: 2,
        title: 'Meeting 2',
        date: dayjs().add(1, 'day').format('YYYY-MM-DD'),
        host: 'Host 2',
        participants: ['Participant 3', 'Participant 4']
      }
    ],
  }),
  actions: {
    async fetchUserSessions(userId) {
      this.userId = userId;
      // 백엔드 구축 후 주석 해제
      // try {
      //   const response = await axios.get(`${API_URL}/users/${userId}/sessions`);
      //   this.sessions = response.data;
      //   await this.fetchAllMeetings();
      // } catch (error) {
      //   console.error('Failed to fetch user sessions:', error);
      // }
    },
    async fetchAllMeetings() {
      // 백엔드 구축 후 주석 해제
      // try {
      //   const meetingPromises = this.sessions.map(session => 
      //     axios.get(`${API_URL}/sessions/${session.id}/meetings`)
      //   );
      //   const meetingsResponses = await Promise.all(meetingPromises);
      //   this.meetings = meetingsResponses.flatMap(response => response.data);
      // } catch (error) {
      //   console.error('Failed to fetch meetings:', error);
      // }
    },
    addMeeting(meeting) {
      this.meetings.push(meeting);
    }
  }
});
