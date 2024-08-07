<template>
  <div class="team-create-page-container">
    <header class="header">
      <span>
        Welcome to
        <span class="highlight">
          TeamCreate
        </span>
        Page
      </span>
    </header>
    <div class="sub-container">
      <div class="top-section">
        <!--제목 설정부분-->
        <div class="teamname-and-icon">
          <section class="teamname-section">
            <div class="team-info">
              <input type="text" v-model="teamName" placeholder="Enter Team Name" class="team-input" />
            </div>
            <span class="centered-text" v-if="teamName.length>0">
              Team Name is <span>&nbsp;</span>
              <span v-if="teamName.length> 0 ">'</span>
              <span class="team-name-text">
                {{ teamName }}
              </span>
              <span v-if="teamName.length> 0 ">'</span>
            </span>
          </section>
          <section class="icon-section">
            <div style="text-align: center;">ICON</div>
            <div class="icon-list">
              <div
                v-for="icon in icons"
                :key="icon"
                :class="['icon-item', { selected: selectedIcon === icon }]"
                @click="selectIcon(icon)"
              >
                {{ icon }}
              </div>
            </div>
          </section>
        </div>
      </div>
      <div class="main-section">
        <!-- 인원 설정 부분-->
         <section class="search-section">
           <div class="search-wrap">
             <div class="search">
               <input type="text" class="searchTerm" placeholder="Which user are you looking for?" v-model="searchQuery"
                 @input="handleInput" @keyup.enter="searchUsers" />
               <!-- <button type="submit" class="searchButton" @click="searchUsers">
                 <font-awesome-icon icon="search" />
               </button> -->
             </div>
             <ul v-if="showUsers && filteredUsers.length" class="results">
               <li v-for="user in filteredUsers" :key="user.userId" class="showingResult" @click="selectUser(user)">
                 <div class="user-info">{{ user.id }}</div>
                 <button @click="selectUser(user)" style="float: right" class="btn btn-primary">
                   select
                 </button>
               </li>
             </ul>
           </div>
         </section>
         <section class="people-section">
          <div class="selected-users">
            <h3>Selected Users</h3>
            <ul  v-if="selectedUsers.length">
              <li v-for="user in selectedUsers" :key="user.id">
                <div class="user-info">{{ user.id }}</div>
                <div class="btn-group">
                  <button class="btn btn-primary" @click="showProfile(user)">Profile</button>
                  <button @click="removeUser(user.id)" class="btn btn-secondary">Remove</button>
                </div>
              </li>
            </ul>
          </div>
         </section>
      </div>
    </div>
      <div>
        <button @click="handleCreateTeam" class="btn btn-success"
          style="margin: auto; justify-content: center; display: flex">
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
const icons = ['🚀', '💻', '💼', '📈', '🦠', '🧪', '📷', '⌨️', '💣','🆕']

const showError = computed(() => errorStore.showError)
const errorMessage = computed(() => errorStore.errorMessage)
const closeError = () => {
  errorStore.hideError()
}

onMounted(async () => {
  
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
  selectedUsers.value.push('man')
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
    console.log(teamName.value, ownerId, selectedIcon.value, userIds)
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

const selectIcon = (icon) => {
  selectedIcon.value = icon
}

</script>

<style scoped>
.team-create-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 1rem;
  box-sizing: border-box;
  background-color: #fcf9fc;
}

.header {
  text-align: center;
  padding: 1rem 0 1.5rem 0;
  font-weight: bolder;
  font-size: xx-large;
}

.highlight {
  color: rgb(166, 125, 247);
}

.sub-container {
  width: 82%;
  margin: 0 auto;
}

.top-section {
  display: flex;
  justify-content: space-between;
  height: auto;
  flex-direction: column;
  padding: 1rem;
}

.teamname-and-icon {
  display: flex;
  justify-content: space-between;
  gap: 2rem;
  width: 100%;
  border-radius: 8px 8px 0 0;
}

.teamname-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  justify-content: center;
  border: 2px solid rgb(232, 231, 234);
}

.centered-text {
  display: flex;
  justify-content: center;
  align-items: center;
}

.icon-section {
  flex: 1.5;
  background-color: #ffffff;
  padding: 1.5rem 1rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  border: 2px dashed rgb(232, 231, 234);
  font-size: small;
}

.icon-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
  gap: 10px;
}

.icon-item {
  cursor: pointer;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.icon-item.selected {
  background-color: #b2c0ff;
}

.main-section {
  display: flex;
  padding: 1rem;
  justify-content: space-between;
  width: 100%;
  gap: 2rem;
  box-sizing: border-box;
  min-height: 250px;
  max-height: 290px;
}

.search-section {
  flex: 1.5;
  background-color: #ffffff;
  padding: 1.5rem 1rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  border: 2px dashed rgb(232, 231, 234);
  font-size: small;
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
  width: 90%;
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 5px;
  font-size: small;
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

.people-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  justify-content: center;
  border: 2px solid rgb(232, 231, 234);
}

.team-info {
  justify-content: center;
  display: flex;
  margin-bottom: 20px;
}

.team-input {
  width: 70%;
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 5px;
}

.icon-select {
  padding: 10px;
}

.selected-users {
  margin-bottom: 20px;
}

.selected-users h3 {
  font-size: large;
  margin-bottom: 10px;
}

.selected-users ul {
  max-height: 180px;
  overflow-y: auto;
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.selected-users li {
  font-size: small;
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-users .user-info {
  font-size: medium;
  font-weight: bold;
}

.selected-users .btn {
  font-size: x-small;
  padding: 5px;
}

.selected-users .btn-group {
  display: flex;
  gap: 5px;
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

.team-name-text {
  color: blue;
  font-weight: bolder;
}

/* Custom scrollbar styles */
.selected-users ul::-webkit-scrollbar {
  width: 8px;
}

.selected-users ul::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.selected-users ul::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.selected-users ul::-webkit-scrollbar-track {
  background-color: #f0f0f0;
}


</style>
