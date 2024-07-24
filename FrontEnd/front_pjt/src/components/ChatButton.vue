<template>
  <div class="chat-button" @click="$emit('toggleChat')">
    💬
  </div>
</template>

<style scoped>
.chat-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #6200ea;
  color: white;
  border: none;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  z-index: 1000;
}
</style>
