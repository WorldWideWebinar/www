<template>
  <div class="team-chat">
    <div class="chat-container">
      <div v-if="!selectedTeamId" class="team-list">
        <div class="team-list-header">Team List</div>
        <ul>
          <li v-for="team in teams" :key="team.id" @click="handleSelectTeam(team.id)">
            {{ team.teamName }}
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
            <img :src="message.sender_profile" class="profile-image" />
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
          <input id="chat-input" type="text" v-model="userInput" placeholder="Type a message..."
            @keyup.enter="sendMessage" />
          <button @click="sendMessage">Send</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useTeamStore } from '@/stores/teamStore';
import { useMessageStore } from '@/stores/chatStore';

const props = defineProps({
  selectedTeamId: Number,
});

const emit = defineEmits(['toggleChat', 'selectTeam']);

let stompClient = null;
const userInput = ref('');
const usedTeamId = ref('');
const userStore = useUserStore();
const teamStore = useTeamStore();
const messageStore = useMessageStore();
const token = userStore.accessToken;


const teams = computed(() => teamStore.teams);
const users = computed(() => userStore.userList);

const messages = ref([]); // 애는 그냥 받는다.


//   { chat_id: 1, sender_id: 1, team_id: 1, content: 'Hello, Team Alpha!', created_at: '2024-08-01T10:00:00Z' },

// 백에서 보내는 정보는 message.body = {meetingId: , content: , timestamp: } 형을 가짐
// db 상에는 chat_id, sender_id, team_id, content, created_at 이라 되어있음


const handleSelectTeam = (teamId) => {
  // 팀 입장 시점
  emit('selectTeam', teamId);



  usedTeamId.value = teamId;
  console.log(teamId)
  // const socket = new SockJS("https://i11a501.p.ssafy.io/api/stomp/chat");
  const socket = new WebSocket('https://i11a501.p.ssafy.io/api/stomp/chat');
  stompClient = Stomp.over(socket);
  stompClient.connect(
    {
      Authorization: `Bearer ${token}`
    },
    function (frame) {
      stompClient.subscribe(
        `/exchange/chat.exchange/chat.key${teamId}`,
        function (message) {
          // console.log('성공')
          // console.log("Received message:", message);
          const messageBody = JSON.parse(message.body);
          // console.log(messageBody);
          showMessage(messageBody);
        }
      );
    },
    function (error) {
      console.error("Connection error: " + error);
    }
  );
};

function sendMessage() {
  const content = userInput.value;
  const teamId = usedTeamId.value;
  const senderId = userStore.userId;
  const contentType = 'text'
  const senderProfile = userStore.userInfo.profileImageUrl;

  if (stompClient && stompClient.connected && !(content.length == 0)) {
    const message = JSON.stringify({ content, teamId, senderId, contentType, senderProfile}); // 
    stompClient.send(`/pub/chat.${teamId}`, {}, message);
    userInput.value = '';
  }
}

function showMessage(content) {
  console.log('수신 확인 ');


  const newMessage = {
    chat_id: messages.value.length + 1,
    sender_id: content.senderId,
    team_id: content.teamId,
    content: content.content,
    created_at: content.createdAt,
    sender_profile: content.senderProfile
  };

  // messages.value.push(newMessage);
  messageStore.addMessage(newMessage);
}

const backToTeamList = () => {
  emit('selectTeam', null);
  stompClient.disconnect();
};


// const filteredMessages = computed(() =>
//   messages.value.filter((message) => message.team_id === props.selectedTeamId)
// );
const filteredMessages = computed(() =>
  messageStore.getMessagesByTeamId(props.selectedTeamId)
);


const getUserProfileImage = (userId) => {
  const user = users.value.find((u) => u.userId === userId);
  return user ? user.profile_image : '';
};

const getUserName = (userId) => {
  const user = users.value.find((u) => u.userId === userId);
  return user ? user.id : 'default';
};

const getTeamName = (teamId) => {
  const team = teams.value.find((t) => t.id === teamId);
  return team ? team.teamName : '';
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const closeChat = () => {
  console.log("Closing chat...");
  if (stompClient && stompClient.connected) {
    stompClient.disconnect();
  }
  userInput.value = '';
  usedTeamId.value = null;
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
  max-height: 325px; /* 원하는 높이 설정 */
  overflow-y: auto; /* 세로 스크롤 추가 */
  margin: 0;
  border-radius: 8px;
}

.team-list ul::-webkit-scrollbar {
  width: 8px; /* 스크롤바 너비 설정 */
}

.team-list ul::-webkit-scrollbar-thumb {
  background-color: #6200ea; /* 스크롤바 색상 */
  border-radius: 4px; /* 스크롤바 둥근 모서리 */
}

.team-list ul::-webkit-scrollbar-track {
  background-color: #f0f0f0; /* 스크롤바 트랙 색상 */
  border-radius: 4px;
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
