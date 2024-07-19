<template>
  <div id="app" class="d-flex">
    <aside class="sidebar d-flex flex-column">
      <button class="btn btn-add" @click="goingHome">
        <span>🏠</span>
      </button>
      <!-- 위에 있는 버튼은 임시로 만들고 수정-->
      <button class="btn btn-add">
        <RouterLink
          class="no-decoration"
          :to="{ name: 'TeamSearchView'}"
        >
          <span>+</span>
        </RouterLink>
      </button>
      <ul class="nav flex-column">
        <li 
          class="nav-item" 
          v-for="team in teams" 
          :key="team.id"
        >
          <RouterLink 
            class="nav-link" 
            :to="{ name: 'ReadyView', params: { name: team.name } }" 
            active-class="active"
          >
            <span class="icon">{{ team.icon }}</span>
            <span class="link-text">{{ team.displayName }}</span>
          </RouterLink>
        </li>
      </ul>
      <div class="spacer"></div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <button class="btn btn-icon">
            <span class="icon">📢</span>
          </button>
        </li>
        <li class="nav-item">
          <button class="btn btn-icon">
            <span class="icon">❔</span>
          </button>
        </li>
      </ul>
    </aside>
    <main class="flex-grow-1">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
import { onMounted, computed } from 'vue';
import { useUserStore } from './stores/userStore';
import { useTeamStore } from './stores/teamStore';
import router from './router';

const userStore = useUserStore();
// const teamStore = useTeamStore();

const goingHome = () => {
  router.push({ name: 'HomeView' });
};

onMounted(async () => {
  await userStore.fetchUserTeamsAndMeetings(userStore.userId);
  console.log('Teams:', userStore.teams); // 디버깅용
  await userStore.fetchAllUsers() 
});

const teams = computed(() => userStore.teams);
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
  padding: 1rem 0 0.5rem 0;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

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

.sidebar .btn-add {
  background-color: #f8bbd0;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  margin: 0 auto 1rem auto;
  font-size: 1.5rem;
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

.spacer {
  flex-grow: 1;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  
}

main {
  flex-grow: 1;
  background-color: #fff;
  margin-left: 60px;
}

body {
  font-family: 'Nanum Gothic', sans-serif;
}
</style>
