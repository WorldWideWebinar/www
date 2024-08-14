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
    nextChatId: 1, // 시작 ID 값
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
      const chatId = this.nextChatId++;
      this.messages.push({ ...message, chat_id: chatId });
    },
    setMessages(newMessages) {
      this.messages = newMessages.map((message, index) => ({ ...message, chat_id: index + 1 }));
    },
    clearMessages() {
      this.messages = [];
      this.nextChatId = 1; // 메시지 클리어 시 ID 초기화
    },
  },
});