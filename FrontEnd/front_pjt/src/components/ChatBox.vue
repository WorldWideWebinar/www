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
          <div v-for="message in messageStore.messages" :key="message.chat_id" class="chat-message"
            :class="{ 'message-from-me': currentUserId == message.senderId, 'message-from-others': currentUserId != message.senderId }">
            <div v-if="currentUserId != message.senderId" style="flex-grow: 1;" class="message-content-wrapper">
              <img :src="message.senderProfile" class="profile-image" />
              <div class="message-content">
                <div class="message-header">
                  <span class="sender-name">{{ getUserName(message.senderId) }}</span>
                  <span class="message-time">{{ formatDate(message.createdAt) }}</span>
                </div>
                <div class="message-body">{{ message.content }}</div>
              </div>
            </div>
            <div v-else style="flex-grow: 1;" class="message-content-wrapper">
              <div class="message-content">
                <div class="message-header">
                  <span class="message-time">{{ formatDate(message.createdAt) }}</span>
                  <span class="sender-name">Me</span>
                </div>
                <div class="message-body">{{ message.content }}</div>
              </div>
              <img :src="message.senderProfile" class="profile-image" />
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
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useTeamStore } from '@/stores/teamStore';
import { useMessageStore } from '@/stores/chatStore';

const props = defineProps({
  selectedTeamId: Number,
});

const emit = defineEmits(['toggleChat', 'selectTeam']);

let stompClient = null;
let subscription = null;
const userInput = ref('');
const usedTeamId = ref(props.selectedTeamId);
const userStore = useUserStore();
const teamStore = useTeamStore();
const messageStore = useMessageStore();
const token = userStore.accessToken;
const currentUserId = userStore.userId;

const teams = computed(() => teamStore.teams);
const users = computed(() => userStore.userList);

const messages = ref([]); // 애는 그냥 받는다.

const handleSelectTeam = async (teamId) => {
  // 팀 입장 시점
  await messageStore.fetchMessagesByTeamId(teamId);
  emit('selectTeam', teamId);
  usedTeamId.value = teamId;
  setupWebSocket(teamId);
};


const setupWebSocket = (teamId) => {
  if (stompClient && stompClient.connected && subscription) {
    console.log('해제')
    subscription.unsubscribe(); // 기존 구독 해제
    subscription = null
  }

  const socket = new WebSocket('https://i11a501.p.ssafy.io/api/stomp/chat');
  stompClient = Stomp.over(socket);
  stompClient.connect(
    {
      Authorization: `Bearer ${token}`
    },
    function (frame) {
      subscription = stompClient.subscribe(
        `/exchange/chat.exchange/chat.key${teamId}`,
        function (message) {
          const messageBody = JSON.parse(message.body);
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
  const contentType = 'TEXT'
  const senderProfile = userStore.userInfo.profileImageUrl;

  if (stompClient && stompClient.connected && !(content.length == 0)) {
    const message = JSON.stringify({ senderId, teamId, content, contentType, senderProfile }); // 
    stompClient.send(`/pub/chat.${teamId}`, {}, message);
    userInput.value = '';
  }
}

function showMessage(content) {
  console.log('수신 확인 ');


  const newMessage = {
    senderId: content.senderId, // 수정된 필드 이름
    teamId: content.teamId, // 수정된 필드 이름
    content: content.content,
    createdAt: content.createdAt, // 수정된 필드 이름
    senderProfile: content.senderProfile // 수정된 필드 이름
  };

  messageStore.addMessage(newMessage);
}

const backToTeamList = () => {
  emit('selectTeam', null);
  if (stompClient && stompClient.connected && subscription) {
    subscription.unsubscribe(); // 구독 해제
    subscription = null; // 구독 객체 초기화
  }
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
  if (stompClient && stompClient.connected && subscription) {
    subscription.unsubscribe(); // 구독 해제
    subscription = null; // 구독 객체 초기화
  }
  emit('toggleChat');
};

const chatContent = ref(null);
watch(() => messageStore.messages, () => {
  nextTick(() => {
    if (chatContent.value) {
      chatContent.value.scrollTop = chatContent.value.scrollHeight;
    }
  });
});

onMounted(() => {
  console.log('열림')
  handleSelectTeam(props.selectedTeamId)
});

onUnmounted(() => {
  if (stompClient && stompClient.connected) {
    // 구독 해제 및 소켓 연결 종료
    if (subscription) {
      subscription.unsubscribe();
      subscription = null;
    }
    stompClient.disconnect(() => {
      console.log('WebSocket disconnected');
    });
  }
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
  max-height: 325px;
  /* 원하는 높이 설정 */
  overflow-y: auto;
  /* 세로 스크롤 추가 */
  margin: 0;
  border-radius: 8px;
}

.team-list ul::-webkit-scrollbar {
  width: 8px;
  /* 스크롤바 너비 설정 */
}

.team-list ul::-webkit-scrollbar-thumb {
  background-color: #6200ea;
  /* 스크롤바 색상 */
  border-radius: 4px;
  /* 스크롤바 둥근 모서리 */
}

.team-list ul::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  /* 스크롤바 트랙 색상 */
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


.chat-content::-webkit-scrollbar {
  width: 8px;
  /* 스크롤바 너비 설정 */
}

.chat-content::-webkit-scrollbar-thumb {
  background-color: #6200ea;
  /* 스크롤바 색상 */
  border-radius: 4px;
  /* 스크롤바 둥근 모서리 */
}

.chat-content::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  /* 스크롤바 트랙 색상 */
  border-radius: 4px;
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

.message-content-wrapper {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: auto;
  margin-right: 10px;
  margin-left: 10px;
}

/* .message-content {
  flex-grow: 1;
} */

.message-from-me {
  background-color: #f9e3f8;
  /* 사용자 보낸 메시지의 배경 색상 */
}

.message-from-others {
  background-color: #e1e1e1;
  /* 다른 사용자가 보낸 메시지의 배경 색상 */
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

.message-from-me .message-content-wrapper .message-body {
  text-align: right;
}

.message-content {
  flex-grow: 1;
}
</style>
