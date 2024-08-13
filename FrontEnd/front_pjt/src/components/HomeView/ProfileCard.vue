<template>
  <div class="profile-card-outer">
    <div class="profile-card-inner">
      <img :src="profileImage" alt="Profile Image" class="profile-image" />
      <h2>{{ name }}</h2>
      <p>Total Meeting Time: {{ totalMeetingTime }} hours</p>
      <h3>Hosted Teams</h3>
      <ul>
        <li v-for="team in hostedTeams" :key="team.id">{{ team.teamName }}</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useTeamStore } from '@/stores/teamStore'

const userStore = useUserStore()
const teamStore = useTeamStore()

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
</script>

<style scoped>
.profile-card-outer {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.profile-card-inner {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.profile-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px;
}
</style>
