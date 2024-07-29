<template>
  <div class="chat-box">
    <div class="chat-header">
      <span>Chat</span>
      <button class="close-btn" @click="$emit('toggleChat')">✖</button>
    </div>
    <div class="chat-content">
      <!-- 여기에 채팅 내용을 추가합니다 -->
    </div>
    <div class="chat-input">
      <input type="text" placeholder="Type a message..." />
      <button>Send</button>
    </div>
  </div>
</template>

<style scoped>
.chat-box {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 300px;
  height: 400px;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #6200ea;
  color: white;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.chat-content {
  flex-grow: 1;
  padding: 10px;
  overflow-y: auto;
}

.chat-input {
  display: flex;
  padding: 10px;
  border-top: 1px solid #ddd;
}

.chat-input input {
  flex-grow: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 5px;
  margin-right: 10px;
}

.chat-input button {
  background-color: #6200ea;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
}
</style>
