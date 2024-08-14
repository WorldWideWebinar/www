<template>
  <div id="app" class="d-flex">
    <aside class="sidebar d-flex flex-column">
      <div class="home">
        <button class="btn btn-home" @click="goingHome">
          <img src="../src/assets/img/chat.png" alt="logo">
        </button>
      </div>
      <div class="nav-container flex-grow-1" @scroll="handleScroll">
        <ul class="nav flex-column">
          <li 
            class="nav-item" 
            v-for="team in teams" 
            :key="team.id"
            @contextmenu.prevent="showDeleteButtonAt($event, team.id)"
          >
            <RouterLink 
              class="nav-link" 
              :to="{ name: 'ReadyView', params: { id: team.id } }" 
              active-class="active"
            >
              <span class="btn-icon">{{ team.emoji }}</span>
              <span class="link-text" :title="team.teamName">{{ team.teamName }}</span>
            </RouterLink>
          </li>
        </ul>
      </div>
      <div class="spacer"></div>
      <div class="add-team">
        <button class="btn btn-add">
          <RouterLink class="no-decoration" :to="{ name: 'TeamCreateView' }">
            <span>+</span>
          </RouterLink>
        </button>
      </div>
    </aside>
    <main class="flex-grow-1">
      <RouterView />
    </main>
    <ChatButton v-if="isLogin" @toggleChat="toggleChat" />
    <ChatBox v-if="isChatOpen" @toggleChat="toggleChat" @selectTeam="selectTeam" :selectedTeamId="selectedTeamId" />
    <ErrorModal v-if="!showError" :message="errorMessage" @close="closeError" />
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
import { onMounted, computed, ref, nextTick, onBeforeUnmount, watch } from 'vue';
import { useUserStore } from './stores/userStore';
import { useTeamStore } from './stores/teamStore';
import router from './router';
import ChatButton from '@/components/ChatButton.vue';
import ChatBox from '@/components/ChatBox.vue';
import ErrorModal from '@/components/ErrorModal.vue';
import { useErrorStore } from './stores/errorStore';
import { handleClickOutside } from './utils';

const errorStore = useErrorStore()
const userStore = useUserStore()
const teamStore = useTeamStore()
const isLogin = computed(() => userStore.isLogin)
const hasFetchedUserInfo = ref(false)
const selectedTeamId = ref(null)
const showScrollIndicator = ref(false)
const showDeleteButton = ref(null)
const deleteButtonStyle = ref({})

const isOwner = (teamId) => {
  const userId = userStore.userId;
  const ownerId = teamStore.teams.find((team) => team.id === teamId)?.ownerId;
  return ownerId !== '' && userId === ownerId;
};

const leaveTeam = (teamId) => {
  teamStore.leaveTeam(teamId);
};

const goingHome = () => {
  router.push({ name: 'HomeView' });
};

const fetchUserTeams = async () => {
  if (isLogin.value && !hasFetchedUserInfo.value) {
    await userStore.fetchUserInfo(userStore.userId);
    const userInfo = userStore.userInfo;

    if (userInfo && Array.isArray(userInfo.teamList) && userInfo.teamList.length > 0) {
      const newTeamIds = userInfo.teamList.filter(
        (teamId) => !teamStore.teams.some((team) => team.id == teamId)
      );
      await Promise.all(newTeamIds.map((teamId) => teamStore.fetchTeamById(teamId)));
    }

    hasFetchedUserInfo.value = true;
  }
};

const showDeleteButtonAt = (event, teamId) => {
  removeExistingDropdown();

  const dropdown = document.createElement('div');
  dropdown.className = 'dropdown';
  dropdown.style.position = 'fixed';
  dropdown.style.top = `${event.clientY}px`;
  dropdown.style.left = `${event.clientX}px`;
  dropdown.style.zIndex = 9999;

  if (isOwner(teamId)) {
    const deleteButton = document.createElement('button');
    deleteButton.innerHTML = 'Delete <br> Team';
    deleteButton.className = 'btn btn-delete';
    deleteButton.onclick = () => {
      deleteTeam(teamId);
      removeExistingDropdown();
    };
    dropdown.appendChild(deleteButton);
  }

  const leaveButton = document.createElement('button');
  leaveButton.innerHTML = 'Leave <br> Team';
  leaveButton.className = 'btn btn-leave';
  leaveButton.onclick = () => {
    leaveTeam(teamId);
    removeExistingDropdown();
  };
  dropdown.appendChild(leaveButton);

  document.body.appendChild(dropdown);
  showDeleteButton.value = teamId;

  document.addEventListener('click', (e) => handleOutsideClick(e, dropdown), { once: true });
};

const handleOutsideClick = (event, dropdown) => {
  if (!dropdown.contains(event.target)) {
    removeExistingDropdown();
  }
};

const removeExistingDropdown = () => {
  const existingDropdowns = document.querySelectorAll('.dropdown');
  if (existingDropdowns.length > 0) {
    existingDropdowns.forEach((dropdown) => dropdown.remove());
  }
  showDeleteButton.value = null;
};

const handleScroll = () => {
  removeExistingDropdown();
};

const deleteTeam = async (teamId) => {
  const currentRoute = router.currentRoute.value;
  const response = await teamStore.deleteTeam(teamId);

  if (response.isSuccess) {
    showDeleteButton.value = null;
    if (currentRoute.params.id == teamId) {
      router.push({ name: 'HomeView' });
    }
  } else {
    console.error(response.message);
  }
};

onMounted(() => {
  fetchUserTeams().then(() => {
    userStore.fetchAllUsers();
  });
});

onBeforeUnmount(() => {
  removeExistingDropdown();
});

const teams = computed(() => teamStore.teams);

const isChatOpen = ref(false);
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value;
};

const showError = computed(() => errorStore.showErrorModal);
const errorMessage = computed(() => errorStore.errorMessage);
const closeError = () => {
  errorStore.hideError();
};

const selectTeam = (teamId) => {
  selectedTeamId.value = teamId;
};

const checkScroll = () => {
  const navContainer = document.querySelector('.nav-container');
  nextTick(() => {
    if (navContainer && navContainer.scrollHeight > navContainer.clientHeight) {
      showScrollIndicator.value = true;
    } else {
      showScrollIndicator.value = false;
    }
  });
};

watch(teams, checkScroll, { immediate: true });

watch(isLogin, (newValue, oldValue) => {
  if (oldValue && !newValue) {
    isChatOpen.value = false;
  }
});

window.addEventListener('resize', checkScroll);
</script>

<style scoped>
#app {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.sidebar {
  width: 80px;
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
  width: 80px;
  margin: 0 auto;
  background-color: #f1d5f7;
}

.sidebar .btn-home {
  margin: 0px auto;
  padding: 0px 5px;
}

.sidebar .btn-home img {
  width: 60px;
  margin: 5px auto;
}

.nav-container {
  flex-grow: 1;
  overflow-y: auto;
  position: relative;
}

.nav-container::-webkit-scrollbar {
  display: none;
}

ul.nav {
  width: 100%;
}

li.nav-item {
  width: 100%;
  border-bottom: 1px dashed rgb(220, 193, 246);
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
  width: 100%;
  text-align: center;
}

.sidebar .nav-link.active,
.sidebar .nav-link:hover {
  background-color: #e1bee7;
  color: #6a1b9a;
}

.sidebar .btn-icon {
  font-size: 1.5rem;
  margin: 5px;
}

.sidebar .link-text {
  margin-top: 0.2rem;
  font-size: 0.7rem;
  word-wrap: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 85%;
  display: block;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  box-orient: vertical;
  white-space: normal;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.dropdown {
  position: fixed;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  background-color: #e1bee7;
  padding: 5px;
  border-radius: 4px;
}

.add-team {
  margin: 0px auto;
  padding: 20px 22px 0px 22px;
  background-color: #f1d5f7;
}

.sidebar .btn-add {
  background-color: #f8bbd0;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  margin: 0 auto 1rem auto;
  font-size: 1.5rem;
}

.no-decoration {
  text-decoration: none;
  color: inherit;
}

.spacer {
  flex-grow: 1;
}

main {
  flex-grow: 1;
  background-color: #fff;
  margin-left: 80px;
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

<style>
/* global style without scoped */
.btn-delete, .btn-leave {
  position: relative;
  display: inline-block;
  background-color: #aaa2b2;
  color: white;
  border: none;
  padding: 5px 5px;
  margin: 2px 0;
  cursor: pointer;
  z-index: 1000;
  text-align: center;
  font-size: 0.6rem;
  font-weight: 600;
  width: 60px;
  border-radius: 15px;
}

.btn-delete:hover, .btn-leave:hover {
  background-color: #7d7783;
  color: white;
}
</style>