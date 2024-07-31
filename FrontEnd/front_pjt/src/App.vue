<template>
  <div id="app" class="d-flex">
    <aside class="sidebar d-flex flex-column">
      <div class="home">
        <button class="btn btn-home" @click="goingHome">
          <img src="../src/assets/img/nav_logo.png" alt="logo">
        </button>
      </div>
      <div class="seperator"></div>
      <ul class="nav flex-column">
        <li 
          class="nav-item" 
          v-for="team in teams" 
          :key="team.id"
        >
          <RouterLink 
            class="nav-link" 
            :to="{ name: 'ReadyView', params: { id: team.id } }" 
            active-class="active"
          >
            <span class="icon">{{ team.icon }}</span>
            <span class="link-text">{{ team.teamName }}</span>
          </RouterLink>
        </li>
      </ul>
      <div class="add-team">
        <button class="btn btn-add">
          <RouterLink class="no-decoration" :to="{ name: 'TeamCreateView'}">
            <span>+</span>
          </RouterLink>
        </button>
      </div>
      <div class="spacer"></div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <button class="btn btn-icon">
            <!-- <span class="icon">📢</span> -->
          </button>
        </li>
        <li class="nav-item">
          <button class="btn btn-icon">
            <!-- <span class="icon">❔</span> -->
          </button>
        </li>
      </ul>
    </aside>
    <main class="flex-grow-1">
      <RouterView />
    </main>
    <ChatButton v-if="isLogin" @toggleChat="toggleChat" />
    <ChatBox v-if="isChatOpen" @toggleChat="toggleChat" />
    <ErrorModal v-if="!showError" :message="errorMessage" @close="closeError" />
  </div>
</template>


<script setup>
import { RouterLink, RouterView } from 'vue-router';
import { onMounted, computed, ref } from 'vue';
import { useUserStore } from './stores/userStore';
import { useTeamStore } from './stores/teamStore';
import router from './router';
import ChatButton from '@/components/ChatButton.vue';
import ChatBox from '@/components/ChatBox.vue';
import ErrorModal from '@/components/ErrorModal.vue';
import { useErrorStore } from './stores/errorStore';

const errorStore = useErrorStore();
const userStore = useUserStore();
const teamStore = useTeamStore();
const isLogin = computed(() => userStore.isLogin);
const hasFetchedUserInfo = ref(false); // 유저 정보가 이미 fetch되었는지 확인

const goingHome = () => {
  router.push({ name: 'HomeView' });
};

onMounted(async () => {
  console.log(userStore.userInfo)
  if (isLogin.value && !hasFetchedUserInfo.value) {
    await userStore.fetchUserInfo(userStore.userId);
    const userInfo = userStore.userInfo;
    if (userInfo && Array.isArray(userInfo.teamList) && userInfo.teamList.length > 0) {
      await Promise.all(
        userInfo.teamList.map(teamId => teamStore.fetchTeamById(teamId))
      );
      console.log('Teams:', teamStore.teams);
    }
    hasFetchedUserInfo.value = true; // 유저 정보 fetch 완료
  }
  console.log('isLogin:', isLogin.value);
});

const teams = computed(() => teamStore.teams);

// 챗봇
const isChatOpen = ref(false);
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value;
};

const showError = computed(() => errorStore.showError);
const errorMessage = computed(() => errorStore.errorMessage);
const closeError = () => {
  errorStore.hideError();
};
</script>

<style scoped>
#app {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.no-decoration {
  text-decoration: none;
  color: inherit;
}

.sidebar {
  width: 70px;
  height: 100vh;
  background-color: #f3e5f5;
  padding: 0;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.sidebar .btn-home,
.sidebar .btn-toggle,
.sidebar .btn-add,
.sidebar .btn-icon {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
}

.sidebar .home {
  width: 70px;
  margin: 0 auto;
}

.sidebar .btn-home {
  margin: 0 auto;
  padding: 0px 5px; /* 간격 조정 */
}

.sidebar .btn-home img {
  width: 60px; /* 크기 조정 */
  margin: 10px auto;
}

/* 구분선 */
.sidebar .seperator {
  padding: 0px;
  margin: 0 10px;
  border-bottom: 3px dashed #000000;
}

/* 팀 목록 */
ul.nav {
  margin-top: 10px;
}

.sidebar .nav-link {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0.4rem 0;
  color: black;
  font-weight: bold;
  background-color: #f3e5f5;
}

.sidebar .nav-link.active,
.sidebar .nav-link:hover {
  background-color: #e1bee7;
  color: #6a1b9a;
}

.sidebar .icon {
  font-size: 1.5rem;
}

.sidebar .link-text {
  margin-top: 0.2rem;
  font-size: 0.75rem;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 팀 추가 */
.sidebar .btn-add {
  background-color: #f8bbd0;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  margin: 0 auto 1rem auto;
  font-size: 1.5rem;
}

.add-team {
  margin-top: 20px;
}

/* 하단 부분과의 구분 */
.spacer {
  flex-grow: 1;
}

main {
  flex-grow: 1;
  background-color: #fff;
  margin-left: 70px;
}

.error-modal .error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.error-modal .error-content p {
  margin: 0 0 10px;
}

.error-modal .error-content button {
  padding: 5px 10px;
  background: #f44336;
  color: white;
  border: none;
  cursor: pointer;
}

.error-modal .error-content button:hover {
  background: #d32f2f;
}
</style>