import { defineStore } from 'pinia'

export const useMessageStore = defineStore('message', {
  state: () => ({
    messages: [],
  }),
  actions: {
    addMessage(message) {
      this.messages.push(message);
    },
    setMessages(newMessages) {
      this.messages = newMessages;
    },
    clearMessages() {
      this.messages = [];
    }
  },
  getters: {
    getMessagesByTeamId: (state) => (teamId) => {
      return state.messages.filter(message => message.team_id === teamId);
    }
  }
});
