<template>
  <div class="profile-card-outer">
    <div class="profile-card-inner">
      <!-- <img :src="profileImage" alt="Profile Image" class="profile-image" /> -->
      <img src="../../assets/img/profile.png" alt="Profile Image" class="profile-image">
      <div class="profile-name">{{ name }}</div>
      <div class="profile-time">
        <span class="profile-header">Total Meeting Time</span><br>
        <span class="profile-detail">{{ totalMeetingTime }} hours</span>
      </div>
      <div class="profile-team">
        <span class="profile-header">Teams</span>
        <ul class="profile-detail profile-teamlist">
          <li v-for="team in hostedTeams" :key="team.id">{{ team.teamName }}</li>
        </ul>
      </div>
      <button @click="goToUserEditView" class="profile-edit-button">Edit Profile</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useTeamStore } from '@/stores/teamStore'
import { useRouter} from 'vue-router'

const userStore = useUserStore()
const teamStore = useTeamStore()
const router = useRouter()

onMounted(async () => {
  // 필요한 경우 여기에서 userInfo 및 팀 정보를 가져옵니다.
  if (!userStore.userInfo.name) {
    await userStore.fetchUserInfo(userStore.userId)
  }
  if (teamStore.teams.length === 0) {
    await Promise.all(userStore.userInfo.teamList.map((teamId) => teamStore.fetchTeamById(teamId)))
  }
})

const name = computed(() => userStore.userInfo.name)
const profileImage = computed(() => userStore.userInfo.profileImageUrl)
const totalMeetingTime = computed(() => userStore.userInfo.totalMeetingTime)

const hostedTeams = computed(() => {
  return teamStore.teams.filter((team) => userStore.userInfo.teamList.includes(team.id))
})

const goToUserEditView = () => {
  router.push({ name: 'UserEditView', params: { userId: userStore.userId }})
}
</script>

<style scoped>
.profile-card-outer {
  display: flex;
  justify-content: center;
  align-items: center;
  max-height: 100vh;
}

.profile-card-inner {
  background-color: #fff;
  height: 526px; /* 높이 조정 */
  padding: 30px 40px 60px;
  text-align: center;
  margin: 0px auto;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden; /* 내부 컨텐츠가 넘칠 때 숨김 처리 */
}

.profile-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
  border: 1px solid black;
}

.profile-name {
  font-size: 1.2rem;
  font-weight: bolder;
  padding-bottom: 15px;
  border-bottom: 2px dashed lightgray;
}

.profile-time {
  margin: 20px auto;
  padding: 0px;
  padding-bottom: 15px;
  border-bottom: 2px dashed lightgray;
}

.profile-header {
  font-weight: bold;
}

.profile-detail {
  font-size: 0.9rem;
  margin: 10px auto;
}

.profile-team {
  margin: 10px auto;
  padding: 0px;
  padding-bottom: 0px;
  text-align: center;
  height: 130px;
}

.profile-teamlist {
  overflow-y: auto; /* 스크롤 추가 */
  height: 80px;
  margin-bottom: 5px;
}

.profile-teamlist::-webkit-scrollbar {
  width: 8px;
}

.profile-teamlist::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.profile-teamlist::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.profile-teamlist::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 4px;
}

.profile-team li {
  list-style-type: none;
  list-style-position: unset;
  text-align: center;
  margin-left: -25px;
  padding: 0px 15px;
  font-size: 0.8rem;
}

.profile-edit-button {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-size: 12px;
  font-weight: bold;
  padding: 12px 35px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

.profile-edit-button:hover {
  background-color: #b380bc;
}
</style>
