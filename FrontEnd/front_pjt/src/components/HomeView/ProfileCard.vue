<template>
  <div class="profile-card-outer">
    <div class="profile-card-inner">
      <img :src="profileImage" alt="Profile Image" class="profile-image" />
      <div class="profile-name">{{ name }}</div>
      <div class="profile-time">
        <span style="font-weight: bold;">Total Meeting Time</span><br>
        <span>{{ totalMeetingTime }} hours</span>
      </div>
      <div class="profile-team">
        <span style="font-weight: bold;">Hosted Teams</span>
        <ul>
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
  padding: 40px 60px;
  text-align: center;
  margin: 0px auto;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden; /* 내부 컨텐츠가 넘칠 때 숨김 처리 */
}

.profile-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px;
  border: 1px solid black;
}

.profile-name {
  font-size: 1.2rem;
  font-weight: bolder;
  padding-bottom: 20px;
  border-bottom: 2px dashed lightgray;
}

.profile-time {
  margin: 20px auto;
  padding: 10px;
  padding-bottom: 30px;
  border-bottom: 2px dashed lightgray;
}

.profile-team {
  margin: 10px auto;
  max-height: calc(100% - 300px); /* 이미지, 이름, 타임, 마진 등을 제외한 높이 */
  padding: 0px;
  overflow-y: auto; /* 스크롤 추가 */
  text-align: center;
  scrollbar-width: thin;
}

.profile-team ul {
  margin-top: 10px;
  padding-bottom: 15px;
}

.profile-team li {
  list-style-type: none;
  list-style-position: unset;
  text-align: center;
  margin-left: -25px;
  padding: 0px 15px;
}
</style>
