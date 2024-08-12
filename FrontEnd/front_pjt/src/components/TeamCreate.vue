<template>
  <div class="container-wrapper">
    <div class="team-create-page-container">
      <span class="container-title">Create Team</span>
      <div class="container-content">
        <div class="top-section">
          <!--제목 설정부분-->
          <div class="teamname-and-icon">
            <section class="teamname-section">
              <div class="team-info">
                <input type="text" class="team-input" v-model="teamName" placeholder="Please enter your team name"/>
              </div>
            </section>
            <section class="icon-section">
              <div class="icon-list">
                <div
                  v-for="icon in icons"
                  :key="icon.slug"
                  :class="['icon-item', { selected: selectedIcon === icon.character }]"
                  @click="selectIcon(icon.character)"
                >
                  {{ icon.character }}
                </div>
              </div>
            </section>
          </div>
        </div>
        <div class="main-section">
          <!-- 인원 설정 부분-->
          <section class="member-section">
            <div class="search-wrap">
              <div class="search">
                <input type="text" class="search-input" placeholder="Which user are you looking for?" v-model="searchQuery"
                  @input="handleInput" @keyup.enter="searchUsers" />
              </div>
              <div v-show="showUsers && filteredUsers.length" class="results-overlay">
                <ul class="results">
                  <li v-for="user in filteredUsers" :key="user.userId" class="showing-result">
                    <div class="user-info">{{ user.id }}</div>
                    <button
                      @click="selectUser(user)"
                      :class="['btn', selectedUsers.includes(user) ? 'btn-already' : 'btn-select']"
                      :disabled="selectedUsers.includes(user)"
                    >
                      {{ selectedUsers.includes(user) ? 'Already Selected' : 'Select' }}
                    </button>
                  </li>
                </ul>
              </div>
            </div>
            <div class="selected-users">
              <ul v-if="selectedUsers.length">
                <li v-for="user in selectedUsers" :key="user.id">
                  <div class="user-info">{{ user.id }}</div>
                  <div class="btn-group">
                    <button class="btns btn-profile btn-full-width" @click="showProfile(user)">Profile</button>
                    <button @click="removeUser(user.id)" class="btns btn-remove btn-full-width">Remove</button>
                  </div>
                </li>
              </ul>
              <div class="user-cnt">{{ selectedUsers.length }} members will be invited.</div>
            </div>
          </section>
        </div>
      </div>
      <div class="btn-container">
        <button @click="handleCreateTeam" class="btn-success">
          Create Team
        </button>
      </div>
    </div>
    <ProfileModal v-if="showProfileModal" :user="selectedUser" @close="showProfileModal = false" />
    <ErrorModal v-if="!showError" :message="errorMessage" @close="closeError" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/userStore'
import { useTeamStore } from '@/stores/teamStore'
import { useErrorStore } from '@/stores/errorStore'
import ProfileModal from '@/components/ProfileModal.vue'
import ErrorModal from '@/components/ErrorModal.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
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
const icons = ref([]) // API를 통해 가져온 이모지 리스트
const apiKey = '52e705268f19ebcfd321b76e47872b9882d407a1'  // 발급받은 API 키 => 추후에 가리기!


const showError = computed(() => errorStore.showError)
const errorMessage = computed(() => errorStore.errorMessage)
const closeError = () => {
  errorStore.hideError()
}

onMounted(async () => {
})

const fetchEmojis = async () => {
  try {
    const response = await axios.get(`https://emoji-api.com/emojis?access_key=${apiKey}`)
    icons.value = response.data
    console.log('Fetched Emojis:', response.data)
  } catch (error) {
    console.error('Failed to fetch emojis:', error)
    errorStore.showError('Failed to fetch emojis.')
  }
}

const fetchUsers = async () => {
  try {
    const response = await axios.get('https://i11a501.p.ssafy.io/api/users')
    userStore.setUserList(response.data)
    console.log('Fetched Users:', response.data)
  } catch (error) {
    console.error('Failed to fetch users:', error)
    errorStore.showError('Failed to fetch users.')
  }
}

const setup = async () => {
  await fetchEmojis()
  if (userStore.userList.length === 0) {
    await fetchUsers()
  }
  console.log('Setup: User List:', userStore.userList)
}

setup()

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
      await router.replace({ name: 'HomeView' })
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
body {
  background: #f6f5f7;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
}

.container-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 900px;
  margin: auto;
}

.team-create-page-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: auto;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  position: relative;
  overflow: hidden;
  width: 900px;
  max-width: 100%;
  height: 630px;
  padding: 20px;
}

.container-title {
  font-weight: bolder;
  font-size: xx-large;
  margin: 10px auto 0px auto;
  border-bottom: 2px dashed rgb(220, 193, 246);
}

.container-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  flex-grow: 1;
  margin: 0 auto 20px auto;
  border-bottom: 2px dashed rgb(220, 193, 246);
}

.top-section, .main-section {
  width: 48%;
  margin: auto;
  font-size: 0.9rem;
  height: 330px;
}

.teamname-and-icon, .member-section {
  width: 80%;
}

/* 팀명 */
.teamname-and-icon {
  justify-content: space-between;
  margin: auto;
}

.teamname-section {
  background-color: #ffffff;
  padding: 0.5rem 0rem;
  border-radius: 8px;
  justify-content: center;
  width: 100%;
  margin: 0 auto;
}

.team-info {
  justify-content: center;
  display: flex;
  margin-bottom: 20px;
  margin: auto;
}

.team-input {
  width: 100%;
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 3px;
  margin: auto;
}

/* 아이콘 */
.icon-section {
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
  font-size: small;
  margin: 0 auto;
  height: 250px;
  overflow-y: auto;
}

.icon-header {
  text-align: center;
  margin: 15px auto;
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

/* 팀원 */
.member-section {
  justify-content: space-between;
  margin: auto;
  background-color: #ffffff;
  padding: 0.5rem 0rem;
  border-radius: 8px;
  justify-content: center;
  margin: 0 auto;
}

.search-wrap {
  width: 100%;
  margin-bottom: 20px;
  position: relative;
}

.search {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.search-input {
  width: 100%;
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 3px;
  margin: auto;
}

.results-overlay {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: white;
  border: 1px solid #ccc;
  border-radius: 0 0 3px 3px;
  border-top: none;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}

.results {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.showing-result {
  padding: 10px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-select {
  background-color: #6a1b9a;
  color: #fff;
  font-size: 0.8rem;
  padding: 5px 10px;
}

.btn-already {
  background-color: #6c757d;
  color: #fff;
  font-size: 0.8rem;
  padding: 5px 10px;
}

/* 선택된 멤버 목록 */
.selected-users {
  margin-bottom: 10px;
  height: 100%;
}

.selected-users ul {
  height: 220px;
  overflow-y: auto;
  list-style-type: none;
  padding: 0;
  margin: 0;
  margin-bottom: 10px;
}

.selected-users li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px;
}

.selected-users .user-info {
  font-size: medium;
  font-weight: bold;
  width: 320px;
}

.selected-users .btn-group {
  display: flex;
  width: 60%;
}

.selected-users .btns {
  font-size: x-small;
  padding: 5px;
  border: none;
  text-align: center;
}

.btn-full-width {
  flex: 1;
}

.btn-profile {
  background-color: #b380bc;
  color: #fff;
  font-size: x-small;
  padding: 5px 10px;
  border-radius: 8px 0 0 8px;
}

.btn-remove {
  background-color: #6c757d;
  color: #fff;
  font-size: x-small;
  padding: 5px 10px;
  border-radius: 0 8px 8px 0;
}

.user-cnt {
  text-align: center;
  font-size: 0.8rem;
  font-weight: bold;
}

.btn-success {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-weight: bold;
  padding: 12px 35px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.btn-success:hover {
  background-color: #b380bc;
}

/* 스크롤바 커스텀 */
.icon-section::-webkit-scrollbar, .results-overlay::-webkit-scrollbar {
  width: 12px;
}

.icon-section::-webkit-scrollbar-thumb, .results-overlay::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 5px;
}

.icon-section::-webkit-scrollbar-thumb:hover, .results-overlay::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.icon-section::-webkit-scrollbar-track, .results-overlay::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 5px;
}


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
  border-radius: 4px;
}

@media (max-width: 992px) {
  .container-wrapper {
    width: 100%;
    margin: auto;
  }

  .team-create-page-container {
    width: 90%;
    margin: auto;
  }

  .container-content {
    flex-direction: column;
    align-items: center;
  }

  .top-section, .main-section {
    width: 100%;
    height: 200px;
  }

  .main-section {
    margin-top: 0px;
  }

  .icon-section {
    height: 120px;
  }

  .results-overlay {
    height: 140px;
  }

  .selected-users ul {
    max-height: 90px;
    overflow-y: auto;
  }
  
}
</style>