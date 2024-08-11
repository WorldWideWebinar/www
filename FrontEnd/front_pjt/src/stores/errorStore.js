import { defineStore } from 'pinia';

export const useErrorStore = defineStore('error', {
  state: () => ({
<<<<<<< HEAD
    showError: true,
=======
    showErrorModal: true,
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
    errorMessage: '',
  }),
  actions: {
    showError(message) {
      this.errorMessage = message;
<<<<<<< HEAD
      this.showError = false;
    },
    hideError() {
      this.showError = true;
=======
      this.showErrorModal = false;
    },
    hideError() {
      this.showErrorModal = true;
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
      this.errorMessage = '';
    }
  }
});