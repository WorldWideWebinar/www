import { defineStore } from 'pinia';
// import axios from 'axios'; // 백엔드 구축 후 주석 해제

// const API_URL = 'https://your-backend-api.com';

export const useSessionStore = defineStore('session', {
  state: () => ({
    sessions: [],
  }),
  actions: {
    async fetchUserSessions(userId) {
      // 가상의 데이터
      this.sessions = [
        { id: 1, name: 'R&D', displayName: 'R&D', icon: '🚀', host: 1 },
        { id: 2, name: 'Development', displayName: '개발', icon: '💻', host: 2 },
        { id: 3, name: 'Purchase', displayName: '구매', icon: '💼', host:1 },
        { id: 4, name: 'Sales', displayName: '영업', icon: '📈', host:2 },
      ];
      console.log('Sessions fetched:', this.sessions);
      // 백엔드 구축 후 주석 해제
      // try {
      //   const response = await axios.get(`${API_URL}/users/${userId}/sessions`);
      //   this.sessions = response.data;
      // } catch (error) {
      //   console.error('Failed to fetch user sessions:', error);
      // }
    },
    addSession(session) {
      this.sessions.push(session);
    },
  },
  getters: {
    getSessionById: (state) => (id) => {
      return state.sessions.find(session => session.id === id);
    },
  },
});