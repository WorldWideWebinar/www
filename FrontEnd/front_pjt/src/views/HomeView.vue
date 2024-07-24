<template>
    <div>
        <HomeSeeing/>
    </div>
</template>

<script setup>
import HomeSeeing from "@/components/HomeView/HomeSeeing.vue"
import { useUserStore } from "@/stores/userStore";
import { onMounted } from 'vue'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.fetchUserTeamsAndMeetings(userStore.userId);
  console.log('Teams:', userStore.teams); // 디버깅용
});
</script>

<style scoped>

</style>
