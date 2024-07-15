<script setup>
import { ref } from 'vue'
import ProfileCard from '../components/ProfileCard.vue';
import Calendar from '../components/Calendar.vue'
import CalendarCard from '../components/CalendarCard.vue'

const selectedEvent = ref(null)

const handleEventSelect = (event) => {
  selectedEvent.value = event
}
</script>

<template>
  <div class="app-container">
    <ProfileCard />
    <Calendar />
    <CalendarCard v-if="selectedEvent" :event="selectedEvent" @close="selectedEvent = null" />
  </div>
    
</template>



<style scoped>
.app-container {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  height: 100vh;
  padding: 20px;
  gap: 100px; /* 카드 사이의 간격 조절 */
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