import { defineStore } from 'pinia';

export const useErrorStore = defineStore('error', {
  state: () => ({
    showError: true,
    errorMessage: '',
  }),
  actions: {
    showError(message) {
      this.errorMessage = message;
      this.showError = false;
    },
    hideError() {
      this.showError = true;
      this.errorMessage = '';
    }
  }
});