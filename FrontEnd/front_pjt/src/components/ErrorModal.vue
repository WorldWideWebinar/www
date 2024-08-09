<template>
  <div v-if="showError" class="error-modal">
    <div class="error-content">
      <p>{{ errorMessage }}</p>
      <button @click="closeError">Close</button>
    </div>
  </div>
</template>

<script>
import { useErrorStore } from '@/stores/errorStore';

export default {
  setup() {
    const errorStore = useErrorStore();

    const closeError = () => {
      errorStore.hideError();
    };

    return {
      showError: errorStore.showError,
      errorMessage: errorStore.errorMessage,
      closeError,
    };
  }
};
</script>

<style>
.error-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  border: 1px solid #ccc;
  padding: 20px;
  z-index: 1000;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.error-content p {
  margin: 0 0 10px;
}

.error-content button {
  padding: 5px 10px;
  background: #f44336;
  color: white;
  border: none;
  cursor: pointer;
}

.error-content button:hover {
  background: #d32f2f;
}
</style>