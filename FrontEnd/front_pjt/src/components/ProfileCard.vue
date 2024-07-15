<template>
  <div class="profile-card">
    <img :src="profileImage" alt="Profile Image" class="profile-image" />
    <h2>{{ name }}</h2>
    <p>Total Meeting Time: {{ totalMeetingTime }} hours</p>
    <h3>Hosted Sessions</h3>
    <ul>
      <li v-for="session in hostedSessions" :key="session.id">{{ session.name }}</li>
    </ul>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';

export default {
  setup() {
    const userStore = useUserStore();

    const name = ref('박준영'); // 임시 데이터
    const profileImage = ref('https://via.placeholder.com/150'); // 임시 데이터
    const totalMeetingTime = computed(() => {
      return userStore.meetings.reduce((total, meeting) => total + meeting.duration, 0); // 임시 계산
    });
    const hostedSessions = ref([
      { id: 1, name: 'Frontend' },
      { id: 2, name: 'CS Study' }
    ]); // 임시 데이터

    onMounted(() => {
      // 사용자 데이터 가져오기 로직 (백엔드 연동 시 사용)
      // userStore.fetchUserData();
    });

    return {
      name,
      profileImage,
      totalMeetingTime,
      hostedSessions
    };
  }
};
</script>

<style scoped>
.profile-card {
  background: #1d252c;
  color: #fff;
  padding: 40px 60px;
  text-align: center;
  margin-right: 20px;
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
