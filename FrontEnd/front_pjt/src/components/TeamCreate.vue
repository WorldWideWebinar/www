<!-- 지금 해야 하는 것들 팀을 생성 하는 것, 팀에 들어가야 할 것, userid, 팀 이름, emoji, 초대 인원-->
<template>
  <div class="centered-container">
    <form @submit.prevent="createTeam">
      <div>
        <label for="team-name">팀 이름:</label>
        <input type="text" id="team-name" v-model="teamName" required />
      </div>
      
      <div>
        <label for="emoji-picker">이모지 선택:</label>
        <div @click="showEmojiPicker = !showEmojiPicker" class="emoji-input">
          <span v-if="selectedEmoji">{{ selectedEmoji }}</span>
          <span v-else>이모지를 선택하세요</span>
        </div>
        <Picker v-if="showEmojiPicker" @emoji-select="selectEmoji" />
      </div>

      <div>
        <label for="user-search">유저 검색:</label>
        <input type="text" id="user-search" v-model="userSearchQuery" @input="onUserSearch" placeholder="유저를 검색하세요">
        <ul v-if="filteredUsers.length && userSearchQuery">
          <li v-for="user in filteredUsers" :key="user.id" @click="inviteUser(user)">
            {{ user.name }}
          </li>
        </ul>
      </div>
      
      <div>
        <label>초대된 유저들:</label>
        <ul>
          <li v-for="(user, index) in invitedUsers" :key="user.id">
            {{ user.name }} <button @click="removeUser(index)">x</button>
          </li>
        </ul>
      </div>
      
      <button type="submit">팀 생성</button>
    </form>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';

const team = ref([]);
const teamName = ref('');
const selectedEmoji = ref('');
const showEmojiPicker = ref(false);
const userSearchQuery = ref('');
const invitedUsers = ref([]);
const allUsers = ref([
  { id: 1, name: 'user1' },
  { id: 2, name: 'user2' },
  { id: 3, name: 'user3' },
  // 더 많은 유저들...
]);

// 필터링된 유저 리스트를 계산
const filteredUsers = computed(() => {
  if (!userSearchQuery.value) {
    return [];
  }
  return allUsers.value.filter(user => user.name.includes(userSearchQuery.value));
});

// 이모지 선택 함수
const selectEmoji = (emoji) => {
  selectedEmoji.value = emoji.native;
  showEmojiPicker.value = false;
};

// 유저 검색 함수
const onUserSearch = (event) => {
  userSearchQuery.value = event.target.value;
};

// 유저 초대 함수
const inviteUser = (user) => {
  if (user && !invitedUsers.value.includes(user)) {
    invitedUsers.value.push(user);
  }
  userSearchQuery.value = ''; // 입력 창 초기화
};

// 유저 제거 함수
const removeUser = (index) => {
  invitedUsers.value.splice(index, 1);
};

// 팀 생성 함수
const createTeam = () => {
  team.value.push({
    teamName: teamName.value,
    selectedEmoji: selectedEmoji.value,
    invitedUsers: invitedUsers.value
  });
  
  console.log('Created team:', team.value);

  // 폼 초기화
  teamName.value = '';
  selectedEmoji.value = '';
  invitedUsers.value = [];
};
</script>

<style scoped>
.centered-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  flex-direction: column;
}

form {
  display: flex;
  flex-direction: column;
}

label {
  margin: 10px 0 5px;
}

input {
  margin-bottom: 10px;
  padding: 8px;
  font-size: 1em;
}

button {
  padding: 10px;
  font-size: 1em;
  cursor: pointer;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  cursor: pointer;
  margin: 5px 0;
}

.emoji-input {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.emoji-input span {
  font-size: 1.5em;
}
</style>