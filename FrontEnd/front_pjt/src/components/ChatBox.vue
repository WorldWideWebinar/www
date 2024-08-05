<template>
  <div class="team-chat">
    <div class="chat-container">
      <div v-if="!selectedTeamId" class="team-list">
        <div class="team-list-header">Team List</div>
        <ul>
          <li v-for="team in teams" :key="team.team_id" @click="handleSelectTeam(team.team_id)">
            {{ team.team_name }}
          </li>
        </ul>
      </div>
      <div v-else class="chat-box">
        <div class="chat-header">
          <button class="back-btn" @click="backToTeamList">⬅</button>
          <span class="selected-team">Chat - {{ getTeamName(selectedTeamId) }}</span>
          <button class="close-btn" @click="closeChat">✖</button>
        </div>
        <div ref="chatContent" class="chat-content">
          <div v-for="message in filteredMessages" :key="message.chat_id" class="chat-message">
            <img :src="getUserProfileImage(message.sender_id)" class="profile-image" />
            <div class="message-content">
              <div class="message-header">
                <span class="sender-name">{{ getUserName(message.sender_id) }}</span>
                <span class="message-time">{{ formatDate(message.created_at) }}</span>
              </div>
              <div class="message-body">{{ message.content }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <input
            id="chat-input"
            type="text"
            v-model="userInput"
            placeholder="Type a message..."
            @keyup.enter="sendMessage"
          />
          <button @click="sendMessage">Send</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';

const props = defineProps({
  selectedTeamId: Number,
});

const emit = defineEmits(['toggleChat', 'selectTeam']);

const teams = ref([
  { team_id: 1, team_name: 'Team Alpha' },
  { team_id: 2, team_name: 'Team Beta' },
  { team_id: 3, team_name: 'Team Gamma' },
]);

const users = ref([
  { user_id: 1, name: 'Alice', profile_image: 'https://via.placeholder.com/40' },
  { user_id: 2, name: 'Bob', profile_image: 'https://via.placeholder.com/40' },
  { user_id: 3, name: 'Charlie', profile_image: 'https://via.placeholder.com/40' },
]);

const messages = ref([
  { chat_id: 1, sender_id: 1, team_id: 1, content: 'Hello, Team Alpha!', created_at: '2024-08-01T10:00:00Z' },
  { chat_id: 2, sender_id: 2, team_id: 1, content: 'Hi Alice!', created_at: '2024-08-01T10:01:00Z' },
  { chat_id: 3, sender_id: 3, team_id: 2, content: 'Team Beta here!', created_at: '2024-08-01T10:02:00Z' },
]);

const userInput = ref('');

const handleSelectTeam = (teamId) => {
  emit('selectTeam', teamId);
};

const backToTeamList = () => {
  emit('selectTeam', null);
};

const sendMessage = () => {
  if (userInput.value.trim()) {
    const newMessage = {
      chat_id: messages.value.length + 1,
      sender_id: 1, // 현재 사용자 ID를 사용
      team_id: props.selectedTeamId,
      content: userInput.value,
      created_at: new Date().toISOString(),
    };
    messages.value.push(newMessage);
    userInput.value = '';
  }
};

const filteredMessages = computed(() =>
  messages.value.filter((message) => message.team_id === props.selectedTeamId)
);

const getUserProfileImage = (userId) => {
  const user = users.value.find((u) => u.user_id === userId);
  return user ? user.profile_image : '';
};

const getUserName = (userId) => {
  const user = users.value.find((u) => u.user_id === userId);
  return user ? user.name : '';
};

const getTeamName = (teamId) => {
  const team = teams.value.find((t) => t.team_id === teamId);
  return team ? team.team_name : '';
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const closeChat = () => {
  console.log("Closing chat...");
  emit('toggleChat');
};

const chatContent = ref(null);
watch(filteredMessages, () => {
  nextTick(() => {
    if (chatContent.value) {
      chatContent.value.scrollTop = chatContent.value.scrollHeight;
    }
  });
});
</script>

<style scoped>

.team-chat {
  position: fixed;
  bottom: 20px;
  right: 20px;
}

.chat-container {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 300px;
  height: 410px;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.team-list {
  width: 100%;
  padding: 10px;
  border-right: 1px solid #ddd;
}

.team-list-header {
  font-weight: bold;
  font-size: 1.2rem;
  text-align: center;
  padding: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid black;
}

.team-list ul {
  list-style: none;
  padding: 0;
}

.team-list li {
  padding: 10px;
  cursor: pointer;
}

.team-list li:hover {
  background-color: #f0f0f0;
}

.chat-box {
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
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

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
}

.selected-team {
  margin-left: -20px;
}

.chat-content {
  flex-grow: 1;
  padding: 10px;
  overflow-y: auto;
  max-height: 300px;
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

.chat-message {
  display: flex;
  align-items: flex-start;
  background-color: #f0f0f0;
  border-radius: 10px;
  padding: 10px;
  margin: 10px 0px;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: auto;
  margin-right: 15px;
}

.message-content {
  flex-grow: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sender-name {
  font-weight: bold;
  font-size: 0.8rem;
}

.message-time {
  font-size: 0.7em;
  color: gray;
}

.message-body {
  font-size: 0.9rem;
  word-wrap: break-word;
  word-break: break-word;
}


</style>
