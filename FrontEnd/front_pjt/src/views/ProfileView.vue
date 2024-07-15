<template>
  <div class="profile-view">
    <Calendar />
    <ProfileCard />
  </div>
</template>

<script setup>
import Calendar from '@/components/Calendar.vue';
import ProfileCard from '@/components/ProfileCard.vue';
</script>

<style scoped>
.profile-view {
  display: flex;
  justify-content: space-between;
  padding: 2rem;
  background-color: #f5f5f5;
}

.profile-card, .calendar-card {
  flex: 1;
  min-width: 300px;
  max-width: 600px;
}

/* 반응형 레이아웃: 화면이 1024px 이하로 작아질 경우 */
@media (max-width: 1024px) {
  .app-container {
    flex-direction: column;
    align-items: center;
  }

  .profile-card, .calendar-card {
    max-width: 100%;
  }
}
</style>

