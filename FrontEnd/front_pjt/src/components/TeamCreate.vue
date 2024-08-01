<template>
  <div style="text-align: center; margin: auto">임시</div>
  <div class="team-creation-wrap">
    <div class="team-info">
      <input type="text" v-model="teamName" placeholder="Enter team name" class="team-input" />
      <select v-model="selectedIcon" class="icon-select">
        <option v-for="icon in icons" :key="icon" :value="icon">{{ icon }}</option>
      </select>
    </div>
    <div class="search-wrap">
      <div class="search">
        <input
          type="text"
          class="searchTerm"
          placeholder="Which user are you looking for?"
          v-model="searchQuery"
          @input="handleInput"
          @keyup.enter="searchUsers"
        />
        <button type="submit" class="searchButton" @click="searchUsers">
          <font-awesome-icon icon="search" />
        </button>
      </div>
      <ul v-if="showUsers && filteredUsers.length" class="results">
        <li
          v-for="user in filteredUsers"
          :key="user.userId"
          class="showingResult"
          @click="selectUser(user)"
        >
          <div class="user-info">{{ user.id }}</div>
          <button @click="selectUser(user)" style="float: right" class="btn btn-primary">
            select
          </button>
        </li>
      </ul>
    </div>
    <div class="selected-users" v-if="selectedUsers.length">
      <h3>Selected Users:</h3>
      <ul>
        <li v-for="user in selectedUsers" :key="user.id">
          {{ user.id }}
          <button class="btn btn-primary" @click="showProfile(user)">프로필 보기</button>
          <button @click="removeUser(user.id)" class="btn btn-secondary">Remove</button>
        </li>
      </ul>
    </div>
    <div>
      <button
        @click="handleCreateTeam"
        class="btn btn-success"
        style="margin: auto; justify-content: center; display: flex"
      >
        Create Team
      </button>
    </div>
  </div>
  <ProfileModal v-if="showProfileModal" :user="selectedUser" @close="showProfileModal = false" />
  <ErrorModal v-if="!showError" :message="errorMessage" @close="closeError" />
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useTeamStore } from '@/stores/teamStore'
import { useErrorStore } from '@/stores/errorStore'
import ProfileModal from '@/components/ProfileModal.vue'
import ErrorModal from '@/components/ErrorModal.vue'

const userStore = useUserStore()
const teamStore = useTeamStore()
const errorStore = useErrorStore()

const searchQuery = ref('')
const showUsers = ref(false)
const showProfileModal = ref(false)
const selectedUser = ref(null)
const selectedUsers = ref([])
const teamName = ref('')
const selectedIcon = ref('🚀')
const icons = ['🚀', '💻', '💼', '📈', '🆕']

const showError = computed(() => errorStore.showError)
const errorMessage = computed(() => errorStore.errorMessage)
const closeError = () => {
  errorStore.hideError()
}

onMounted(async () => {
  await userStore.fetchAllUsers()
})

const filteredUsers = computed(() =>
  userStore.userList.filter((user) =>
    user.id.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
)

const handleInput = () => {
  showUsers.value = searchQuery.value.length > 0
}

const searchUsers = () => {
  showUsers.value = filteredUsers.value.length > 0
}

const selectUser = (user) => {
  if (!selectedUsers.value.includes(user)) {
    selectedUsers.value.push(user)
  }
  searchQuery.value = ''
  showUsers.value = false
}

const removeUser = (userId) => {
  selectedUsers.value = selectedUsers.value.filter((user) => user.id !== userId)
}

const handleCreateTeam = async () => {
  if (teamName.value.trim() && selectedUsers.value.length && selectedIcon.value) {
    const userIds = selectedUsers.value.map((user) => user.id)
    const ownerId = userStore.userId
    try {
      await teamStore.createTeam(teamName.value, ownerId, selectedIcon.value, userIds)
      // Reset fields
      teamName.value = ''
      selectedUsers.value = []
      selectedIcon.value = '🚀'
    } catch (error) {
      errorStore.showError('Failed to create team.')
    }
  } else {
    errorStore.showError('Please enter a team name, select users, and choose an icon.')
  }
}

const showProfile = (user) => {
  selectedUser.value = user
  showProfileModal.value = true
}
</script>
요약
<style scoped>
.team-creation-wrap {
  max-width: 600px;
  margin: 0 auto;
}

.search-wrap {
  width: 100%;
  margin-bottom: 20px;
}

.search {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.searchTerm {
  width: 70%;
  padding: 10px;
  border: 2px solid #ccc;
  border-right: none;
  border-radius: 5px 0 0 5px;
}

.searchButton {
  padding: 10px;
  border: 2px solid #ccc;
  border-left: none;
  background-color: #333;
  color: #fff;
  border-radius: 0 5px 5px 0;
  cursor: pointer;
}

.results {
  list-style-type: none;
  padding: 0;
  margin: 0;
  border: 1px solid #ccc;
  max-height: 200px;
  overflow-y: auto;
}

.showingResult {
  padding: 10px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.team-info {
  justify-content: center;
  display: flex;
  margin-bottom: 20px;
}

.team-input {
  width: 70%;
  padding: 10px;
  margin-right: 10px;
}

.icon-select {
  padding: 10px;
}

.selected-users {
  margin-bottom: 20px;
}

.btn {
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: #fff;
}

.btn-secondary {
  background-color: #6c757d;
  color: #fff;
}

.btn-success {
  background-color: #28a745;
  color: #fff;
}
</style>
