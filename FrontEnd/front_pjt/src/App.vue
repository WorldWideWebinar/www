<template>
  <div id="app" class="d-flex">
    <aside class="sidebar d-flex flex-column">
      <div class="home">
        <button class="btn btn-home" @click="goingHome">
          <img src="../src/assets/img/chat.png" alt="logo">
        </button>
      </div>
      <div class="nav-container flex-grow-1">
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
            <div v-show="showDeleteButton === team.id" :ref="(el) => {addEventClickOutside(el)}" class="dropdown">
              <button @click="deleteTeam(team.id)" class="btn btn-delete">Delete <br> Team</button>
            </div>
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
import { RouterLink, RouterView } from 'vue-router'
import { onMounted, computed, ref, watch, nextTick, onBeforeUnmount } from 'vue'
import { useUserStore } from './stores/userStore'
import { useTeamStore } from './stores/teamStore'
import router from './router'
import ChatButton from '@/components/ChatButton.vue'
import ChatBox from '@/components/ChatBox.vue'
import ErrorModal from '@/components/ErrorModal.vue'
import { useErrorStore } from './stores/errorStore'
import { handleClickOutside } from './utils'

const errorStore = useErrorStore()
const userStore = useUserStore()
const teamStore = useTeamStore()
const isLogin = computed(() => userStore.isLogin)
const hasFetchedUserInfo = ref(false)
const selectedTeamId = ref(null)
const showScrollIndicator = ref(false)
const showDeleteButton = ref(null)
const deleteButtonStyle = ref({})
const dropdownRef = ref(null)


const goingHome = () => {
  router.push({ name: 'HomeView' })
}

const fetchUserTeams = async () => {
  if (isLogin.value && !hasFetchedUserInfo.value) {
    await userStore.fetchUserInfo(userStore.userId);
    const userInfo = userStore.userInfo;

    if (userInfo && Array.isArray(userInfo.teamList) && userInfo.teamList.length > 0) {
      const newTeamIds = userInfo.teamList.filter(teamId => !teamStore.teams.some(team => team.id == teamId));
      await Promise.all(newTeamIds.map((teamId) => teamStore.fetchTeamById(teamId)));
    }

    hasFetchedUserInfo.value = true;
  }
};

const showDeleteButtonAt = (event, teamId) => {
  deleteButtonStyle.value = {
    position: 'fixed',
    zIndex: 1000,
  };
  showDeleteButton.value = teamId;

  // nextTick(() => {
  //   if (dropdownRef.value) {
  //     document.addEventListener('click', outsideClickHandler);
  //   }
  // });
};

const deleteTeam = async (teamId) => {
  const currentRoute = router.currentRoute.value;
  const response = await teamStore.deleteTeam(teamId);

  if (response.isSuccess) {
    showDeleteButton.value = null;
    removeOutsideClickListener();

    if (currentRoute.params.id == teamId) {
      router.push({ name: 'HomeView' });
    }
  } else {
    console.error(response.message);
  }
};

// // 외부 클릭 감지 핸들러
// const outsideClickHandler = handleClickOutside(dropdownRef, () => {
//   console.log("click outside click");
//   showDeleteButton.value = null;
//   removeOutsideClickListener();
// });

const addEventClickOutside = (el)=>{
  document.addEventListener('click',handleClickOutside(el,()=>{
      showDeleteButton.value = null;
    })
  )
}

const removeOutsideClickListener = (handler) => {
  document.removeEventListener('click', handler);
};

onMounted(() => {
  fetchUserTeams().then(() => {
    userStore.fetchAllUsers();
  });
});

onBeforeUnmount(() => {
  removeOutsideClickListener();
});


const teams = computed(() => teamStore.teams)

// 챗봇

const isChatOpen = ref(false)
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value
  // if (!isChatOpen.value) {
  //   selectedTeamId.value = null;
  // }
}

const showError = computed(() => errorStore.showErrorModal)
const errorMessage = computed(() => errorStore.errorMessage)
const closeError = () => {
  errorStore.hideError()
}
const selectTeam = (teamId) => {
  selectedTeamId.value = teamId
}

onMounted(() => {
  const navContainer = ref(null)
  const checkScroll = () => {
    nextTick(() => {
      if (navContainer.value && navContainer.value.scrollHeight > navContainer.value.clientHeight) {
        showScrollIndicator.value = true
      } else {
        showScrollIndicator.value = false
      }
    })
  }

  watch(teams, checkScroll, { immediate: true })
  window.addEventListener('resize', checkScroll)
})

watch(isLogin, (newValue, oldValue) => {
  if (oldValue && !newValue) {
    isChatOpen.value = false;
  }
});
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
  /* 간격 조정 */
}

.sidebar .btn-home img {
  width: 60px;
  /* 크기 조정 */
  margin: 5px auto;
}

/* 구분선 */
.sidebar .seperator {
  padding: 0px;
  margin: 0px;
  border-bottom: 3px dashed #000000;
}

/* 팀 목록 */
.nav-container {
  flex-grow: 1;
  overflow-y: auto;
  /* 세로 스크롤 가능 */
  position: relative;
}

.nav-container::-webkit-scrollbar {
  display: none;
}

ul.nav {
  /* margin-top: 10px; */
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
  /* 길면 줄바꿈 */
  overflow: hidden;
  /* 넘치는 부분 숨기기 */
  text-overflow: ellipsis;
  /* 넘치는 부분을 생략(...)으로 표시 */
  max-width: 85%;
  /* 최대 너비를 설정하여 오른쪽 영역 침범 방지 */
  display: block;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  /* 표시할 최대 줄 수 */
  -webkit-box-orient: vertical;
  line-clamp: 2;
  box-orient: vertical;
  white-space: normal;
  /* 두 줄로 표시 */
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 팀 삭제 */
.dropdown {
  position: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  left: 80px;
  margin-top: -75px;
}

.btn-delete {
  background-color: #e1bee7;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
}

.btn-delete:hover {
  background-color: #8c8593;
}

/* 팀 추가 */
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

/* 하단 부분과의 구분 */
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
