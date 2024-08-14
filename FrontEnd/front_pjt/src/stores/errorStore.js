import { defineStore } from 'pinia';

export const useErrorStore = defineStore('error', {
  state: () => ({
    showErrorModal: true,
    errorMessage: '',
  }),
  actions: {
    showError(message) {
      this.errorMessage = message;
      this.showErrorModal = false;
    },
    hideError() {
      this.showErrorModal = true;
      this.errorMessage = '';
    }
  }
});