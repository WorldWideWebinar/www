<template>
  <div class="profile-view">
    <div class="section-calendar">
      <Calendar />
    </div>
    <div class="section-profilecard">
      <ProfileCard />
    </div>
  </div>
</template>

<script setup>
import Calendar from '@/components/HomeView/Calendar.vue';
import ProfileCard from '@/components/HomeView/ProfileCard.vue';
</script>

<style scoped>
.profile-view {
  display: flex;
  justify-content: center;
  background-color: #f5f5f5;
  margin: auto;
  min-height: 100vh;
}

.section-calendar, .section-profilecard {
  margin: auto 50px; /* 둘 사이의 간격 */
}

/* 반응형 레이아웃: 화면이 992px 이하로 작아질 경우 */
@media (max-width: 992px) {
  .app-container {
    flex-direction: column;
    align-items: center;  
  }

  .section-profilecard {
    display: none;
  }
}
</style>

