<template>
  <div class="landing-page-container">
    <HomeSeeing/>
  </div>
</template>

<script setup>
import HomeSeeing from "@/components/HomeView/HomeSeeing.vue"
import { useUserStore } from "@/stores/userStore";
import { useTeamStore } from "@/stores/teamStore";
import { onMounted } from 'vue'

const userStore = useUserStore();
const teamStore = useTeamStore();

// onMounted(async () => {
//   if (userStore.isLogin) {
//     await userStore.fetchUserInfo(userStore.userId);
//     await teamStore.fetchUserTeams(userStore.userInfo.teamList);
//     console.log('Teams:', teamStore.teams); // 디버깅용
//   }
// });
</script>

<style scoped>
.landing-page-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
  background-color: #fcf9fc;
}
</style>