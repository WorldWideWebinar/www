<template>
  <div class="profile-card-outer">
    <div class="profile-card-inner">
      <img :src="profileImage" alt="Profile Image" class="profile-image" />
      <h2>{{ name }}</h2>
      <p>Total Meeting Time: {{ totalMeetingTime }} hours</p>
      <h3>Hosted Teams</h3>
      <ul>
        <li v-for="team in hostedTeams" :key="team.id">{{ team.name }}</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useMeetingStore } from '@/stores/meetingStore';

const userStore = useUserStore();
const meetingStore = useMeetingStore();

const name = ref('박준영'); // 임시 데이터
const profileImage = ref('https://via.placeholder.com/150'); // 임시 데이터

const totalMeetingTime = computed(() => {
  return meetingStore.meetings.reduce((total, meeting) => total + (meeting.duration || 0), 0);
});

const hostedTeams = computed(() => {
  return userStore.teams.filter(team => team.host === userStore.userId);
});

</script>

<style scoped>
.profile-card-outer {
  background: #C5CAE9; /* 보색 2 */
  padding: 10px;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: 20px;
}

.profile-card-inner {
  background: #ffffff;
  color: #1d252c;
  padding: 40px 60px;
  text-align: center;
  border-radius: 20px;
}

.profile-image {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  margin-bottom: 20px;
}

h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

h3 {
  font-size: 20px;
  margin: 20px 0 10px;
}

ul {
  list-style-type: none;
  padding: 0;
}

ul li {
  margin: 5px 0;
}

p {
  font-size: 18px;
  margin: 10px 0;
}
</style>
