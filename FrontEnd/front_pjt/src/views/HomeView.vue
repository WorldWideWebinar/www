<template>
    <div class="landing-page-container">
      <HomeSeeing/>
    </div>
</template>

<script setup>
import HomeSeeing from "@/components/HomeSeeing.vue"
import { useUserStore } from "@/stores/userStore";
import { onMounted } from 'vue'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.fetchUserTeamsAndMeetings(userStore.userId);
  console.log('Teams:', userStore.teams); // 디버깅용
});
</script>

<style scoped>
.landing-page-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 1rem 0;
  box-sizing: border-box;
  background-color: #fcf9fc;
}
</style>
