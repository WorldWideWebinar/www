<template>
  <dialog ref="loading" @close="model = false">
    <div class="spinner"></div>
  </dialog>
</template>

<script setup>
import { ref, watch, onMounted, defineModel } from 'vue'

const model = defineModel({ required: true })

const loading = ref(null)

onMounted(control)

watch(model, control)

function control() {
  if (model.value) loading.value.showModal()
  else loading.value.close()
}
</script>

<style scoped>
dialog {
  outline: none;
  border: none;

  background: transparent;
}

.spinner {
  display: flex;
  align-items: center;
  justify-content: center;

  --square-size: 48px;

  width: var(--square-size);
  height: var(--square-size);

  border: 5px solid #f3f3f3;
  border-top: 5px solid #3498db;
  border-radius: 50%;

  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>