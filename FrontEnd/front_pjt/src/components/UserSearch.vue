<template>
  <div class="search-wrap">
    <div class="search">
      <input type="text" class="searchTerm" placeholder="Which user are you looking for?" v-model="searchQuery"
        @input="handleInput" @keyup.enter="searchUsers" />
      <button type="submit" class="searchButton" @click="searchUsers">
        <font-awesome-icon icon="search" />
      </button>
    </div>
    <ul v-if="showUsers && filteredUsers.length" class="results">
      <li v-for="user in filteredUsers" :key="user.id" class="showingResult" @click="selectUser(user)">
        <div class="user-info">
          {{ user.username }} ({{ user.email }})
        </div>
        <button @click="selectUser(user)" style="float: right;" class="btn btn-primary">select</button>
      </li>
    </ul>
  </div>
  <div class="results-wrap" v-if="showResults">
    <!-- <UserSearchResult v-for="user in displayedUsers" :key="user.id" :user="user" /> -->
    <div v-for="user in displayedUsers" :key="user.id" class="user-card">
      <h2 class="title">{{ user.username }}</h2>
      <p>{{ user.email }}</p>
      <button class="btn btn-primary" @click="showProfile(user)">프로필 보기</button>
      <button class="btn btn-primary" style="float: right;" @click="addMember(user)">Add</button>
    </div>
  </div>
  <ProfileModal v-if="showProfileModal" :user="selectedUser" @close="showProfileModal = false" />
</template>

<script setup>
import { ref, computed } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
// import UserSearchResult from '@/components/UserSearchResult.vue';
import ProfileModal from '@/components/ProfileModal.vue'

const userStore = useUserStore();
const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const searchQuery = ref('');
const showUsers = ref(false);
const showResults = ref(false);
const selectedUser = ref(null);
const showProfileModal = ref(false);


const filteredUsers = computed(() =>
  userStore.userList.filter(user =>
    user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
);

const displayedUsers = computed(() => {
  if (selectedUser.value) {
    return [selectedUser.value];
  } else {
    return filteredUsers.value;
  }
});

const handleInput = () => {
  showUsers.value = searchQuery.value.length > 0;
};

const searchUsers = () => {
  if (filteredUsers.value.length) {
    selectedUser.value = null;
    showResults.value = true;
    showUsers.value = false;
  } else {
    showResults.value = false;
  }
};

const selectUser = (user) => {
  selectedUser.value = user;
  searchQuery.value = user.username;
  showUsers.value = false;
  showResults.value = true;
  console.log(user)
};

const showProfile = user => {
  selectedUser.value = user;
  showProfileModal.value = true;
};

const addMember = (user) => {
  const teamId = 1;
  console.log(user.id)
  teamStore.addMembertoTeam(user.id, teamId);
}
</script>

<style scoped>
.user-card {
  border: 1px solid #e1bee7;
  border-radius: 5px;
  padding: 20px;
  background: #fff;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

body {
  background: #f2f2f2;
  font-family: 'Open Sans', sans-serif;
}

.search-wrap {
  width: 50%;
  position: absolute;
  top: 20%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.search {
  width: 100%;
  position: relative;
  display: flex;
}

.searchTerm {
  width: 100%;
  border: 3px solid #e1bee7;
  border-right: none;
  padding: 5px;
  height: 36px;
  border-radius: 5px 0 0 5px;
  outline: none;
  color: #9dbfaf;
  transition: all 0.3s ease;
}

.searchTerm:focus {
  color: #e1bee7;
  width: 200%;
}

.searchButton {
  width: 40px;
  height: 36px;
  border: 3px solid #e1bee7;
  border-left: none;
  background: #f8bbd0;
  text-align: center;
  color: #fff;
  border-radius: 0 5px 5px 0;
  cursor: pointer;
  font-size: 20px;
}

.searchButton:hover {
  background: #e1bee7;
}

.results {
  list-style-type: none;
  padding: 0;
  margin: 0;
  position: absolute;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #e1bee7;
  border-radius: 0 0 5px 5px;
  background: #fff;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  top: 100%;
}

.results li {
  padding: 10px;
  cursor: pointer;
}

.results li:hover {
  background: #e1bee7;
}

.results-wrap {
  width: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, 0);
  padding-top: 20px;
}

.showingResult {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: inline-block;
  vertical-align: middle;
  text-align: center;
}
</style>
