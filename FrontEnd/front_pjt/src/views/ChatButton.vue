<template>
  <div class="chat-button" @click="$emit('toggleChat')">
    <img src="../../src/assets/img/logo.png" alt="logo">
  </div>
</template>

<style scoped>
.chat-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: rgba(98, 0, 234, 0.5);
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

.chat-button img {
  width: 40px;
}
</style>
