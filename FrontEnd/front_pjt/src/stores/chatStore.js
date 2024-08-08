import { defineStore } from 'pinia'
import axiosInstance from '@/axios';

// export const useMessageStore = defineStore('message', {
//   state: () => ({
//     messages: [],
//   }),
//   actions: {
//     addMessage(message) {
//       this.messages.push(message);
//     },
//     setMessages(newMessages) {
//       this.messages = newMessages;
//     },
//     clearMessages() {
//       this.messages = [];
//     }
//   },
//   getters: {
//     getMessagesByTeamId: (state) => (teamId) => {
//       return state.messages.filter(message => message.team_id === teamId);
//     }
//   }
// });
export const useMessageStore = defineStore('message', {
  state: () => ({
    messages: [],
  }),
  actions: {
    async fetchMessagesByTeamId(teamId) {
      try {
        const response = await axiosInstance.get(`/api/chats/team/${teamId}`);
        this.setMessages(response.data);
      } catch (error) {
        console.error('Failed to fetch messages:', error);
      }
    },
    addMessage(message) {
      this.messages.push(message);
    },
    setMessages(newMessages) {
      this.messages = newMessages;
    },
    clearMessages() {
      this.messages = [];
    }
  }
});