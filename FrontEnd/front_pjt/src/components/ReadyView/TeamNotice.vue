<template>
  <div class="container">
    <section class="notice-section">
      <div class="notice-header">
        <h5 style="font-weight: bolder"><span class="icon">🏴</span> Notice</h5>
      </div>
      <div class="notice-content">
        <table v-if="todayMeetings.length > 0" class="notice-table">
          <tbody>
            <tr v-for="meeting in todayMeetings" :key="meeting.id">
              <td>{{ formatTime(meeting.start_at) }} - {{ formatTime(meeting.end_at) }}</td>
              <td class="bold meeting-name">{{ meeting.name }}</td>
              <td class="join-td">
                <button v-if="isOwner" @click="handleStartConference(meeting.meeting_id)" class="join-button">
                  <img class="play-button" src="@/assets/img/playbutton.png" alt="play">
                </button>
                <button v-else @click="handleJoinConference(meeting.meeting_id)" class="join-button">
                  <img class="play-button" src="@/assets/img/play.png" alt="play">
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="notice-item">
          <p class="no-meeting">There's no meeting :)</p>
        </div>
      </div>
    </section>
    <section class="intro-section">
      <div class="total-meeting-hours">
        <p>We have meetings for {{ totalMeetingHours.toFixed(2) }} hours</p>
        <div class="meeting-hours-bar">
          <div class="meeting-hours-segment prev-meetings" :style="{ width: (prevMeetingHours / totalMeetingHours) * 100 + '%' }" v-if="prevMeetingHours > 0"></div>
          <div class="meeting-hours-segment today-meetings" :style="{ width: (todayMeetingHours / totalMeetingHours) * 100 + '%' }" v-if="todayMeetingHours > 0"></div>
          <div class="meeting-hours-segment next-meetings" :style="{ width: (nextMeetingHours / totalMeetingHours) * 100 + '%' }" v-if="nextMeetingHours > 0"></div>
        </div>
        <div class="meeting-hours-legend">
          <div class="legend-item">
            <span class="legend-color prev-meetings"></span>
            <span class="legend-label">Previous <br> {{ formatHours(prevMeetingHours) }} hours</span>
          </div>
          <div class="legend-item">
            <span class="legend-color today-meetings"></span>
            <span class="legend-label">Today <br> {{ formatHours(todayMeetingHours) }} hours</span>
          </div>
          <div class="legend-item">
            <span class="legend-color next-meetings"></span>
            <span class="legend-label">Next <br> {{ formatHours(nextMeetingHours) }} hours</span>
          </div>
        </div>
      </div>
      <div class="department-info">
        <table class="department-table">
          <tbody>
            <tr>
              <td style="position: relative;">
                <div class="members-row" @click="toggleMemberListDropdown" ref="memberDropdown">
                  {{ members.length }} members
                  <button v-if="isOwner" class="add-member-btn" @click.stop="toggleInviteMemberInput">+</button>
                </div>
                <ul v-show="showMemberListDropdown" class="members-dropdown dropdown">
                  <li v-for="member in members" :key="member.name" class="member">
                    {{ member.name }}
                  </li>
                </ul>
                <transition name="slide-fade">
                  <div v-if="showInviteMemberInput" class="invite-member-input" ref="inviteInput">
                    <button @click="cancelInvite" class="btns btn-close"></button>
                    <div class="invite-member-row">
                      <input class="search-member" v-model="newMemberId" placeholder="Enter ID" />
                    </div>
                    <div class="search-results">
                      <div v-for="user in filteredSearchResults" :key="user.userId" class="search-result-item">
                        <span>{{ user.id }}</span>
                        <button @click="inviteMember(user)" class="btn-invite">Invite</button>
                      </div>
                    </div>
                  </div>
                </transition>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { formatTime, handleClickOutside } from '@/utils.js';
import { useUserStore } from '@/stores/userStore.js'
import { useRouter, useRoute } from 'vue-router'
import { useSessionStore } from '@/stores/sessionStore';

const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const todayMeetings = computed(() => {
  const now = new Date();

  return teamStore.groupedMeetings.TODAY.filter(meeting => {
    const endAt = new Date(meeting.end_at);
    return endAt >= now;
  });
});
const userStore = useUserStore();
const showMemberListDropdown = ref(false);
const showInviteMemberInput = ref(false);
const newMemberId = ref('');
const members = computed(() => teamStore.teamUserInfo);
const isOwner = computed(() => teamStore.teamInfo.ownerId == userStore.userId);
const router = useRouter();
const route = useRoute();
const sessionStore = useSessionStore();

function formatDate(meetingList) {
    return meetingList.reduce((total, meeting) => {
      const start = new Date(meeting.start_at);
      const end = new Date(meeting.end_at);
      return total + (end - start) / (1000 * 60 * 60);
    }, 0)
}
const prevMeetingHours = computed(() => formatDate(teamStore.groupedMeetings.PREV))
const todayMeetingHours = computed(() => formatDate(teamStore.groupedMeetings.TODAY))
const nextMeetingHours = computed(() => formatDate(teamStore.groupedMeetings.NEXT))
const totalMeetingHours = computed(() => prevMeetingHours.value + todayMeetingHours.value + nextMeetingHours.value);

const formatHours = (hours) => {
  return hours % 1 === 0 ? Math.floor(hours) : hours.toFixed(2);
};


// 여기서 각 요소에 대한 ref를 설정합니다.
const memberDropdown = ref(null);  // memberDropdown 요소에 대한 ref
const inviteInput = ref(null);  // inviteInput 요소에 대한 ref

const toggleMemberListDropdown = () => {
  showMemberListDropdown.value = !showMemberListDropdown.value;
};

const toggleInviteMemberInput = () => {
  console.log(teamStore.teamInfo)
  showInviteMemberInput.value = !showInviteMemberInput.value;
};

const filteredUsers = computed(() => {
  const teamUserIds = teamStore.teamUserList;
  return userStore.userList.filter(user => !teamUserIds.includes(user.userId));
});

// 검색된 사용자 목록 필터링
const filteredSearchResults = computed(() => {
  if (!newMemberId.value) {
    return [];
  }
  return filteredUsers.value.filter(user => user.id.toLowerCase().includes(newMemberId.value.toLowerCase()));
});

const inviteMember = async (user) => {
  const teamId = route.params.id
  try {
    await teamStore.addMemberToTeam(user, teamId);
    teamStore.loadData(teamId)
  } catch (error) {
    // 오류 처리 (예: 오류 메시지 표시)
  }
};

const cancelInvite = () => {
  showInviteMemberInput.value = false;
  newMemberId.value = '';
};

const closeMemberListDropdown = () => {
  showMemberListDropdown.value = false;
};

const closeInviteInput = () => {
  showInviteMemberInput.value = false;
};

let removeMemberDropdownListener;
let removeInviteInputListener;

onMounted(() => {
  const memberDropdownHandler = handleClickOutside(memberDropdown, closeMemberListDropdown);
  const inviteInputHandler = handleClickOutside(inviteInput, closeInviteInput);

  document.addEventListener('click', memberDropdownHandler);
  document.addEventListener('click', inviteInputHandler);

  removeMemberDropdownListener = () => document.removeEventListener('click', memberDropdownHandler);
  removeInviteInputListener = () => document.removeEventListener('click', inviteInputHandler);
});

onBeforeUnmount(() => {
  if (removeMemberDropdownListener) removeMemberDropdownListener();
  if (removeInviteInputListener) removeInviteInputListener();
});


const handleStartConference = async (meetingId) => {
  const userId = userStore.userId;
  try {
    let sessionId = sessionStore.sessionId; // 이미 저장된 sessionId 확인

    if (!sessionId) {
      // sessionId가 없는 경우 새로운 세션 시작
      sessionId = await sessionStore.startConference(meetingId, userId);
    }

    const token = await sessionStore.joinConference(sessionId);
    router.push({ name: 'ConferenceView', params: { sessionId, token } });
  } catch (error) {
    console.error('Failed to start conference:', error);
  }
};

const handleJoinConference = async (sessionName) => {
  try {
    const token = await sessionStore.joinConference(sessionName);
    router.push({ name: 'ConferenceView', params: { sessionId: sessionName, token } });
  } catch (error) {
    console.error('Failed to join conference:', error);
  }
};
</script>


<style scoped>
template {
  width: 100%;
  box-sizing: border-box;
  margin: 0 auto;
}

.container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  gap: 2rem;
  box-sizing: border-box;
  padding: 0rem;
  margin: 0 auto;
}

.notice-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-sizing: border-box;
}

.intro-section {
  flex: 1.5;
  background-color: #ffffff;
  padding: 1.5rem 1rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  border: 2px dashed rgb(232, 231, 234);
  font-size: small;
  box-sizing: border-box;
}

.notice-header,
.meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.notice-header h5 .icon {
  margin-right: 5px;
}

.notice-content {
  max-height: 125px; /* 원하는 최대 높이 설정 */
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 0px 10px;
  background-color: #f9f9f9;
  font-size: medium;
}

.notice-content::-webkit-scrollbar {
  width: 8px;
}

.notice-content::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.notice-content::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.notice-content::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 4px;
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
}

.notice-table th,
.notice-table td {
  padding: 8px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
}

.notice-table th {
  background-color: #f0f0f0;
  font-weight: bold;
}

.notice-table .meeting-name {
  max-width: 200px;
  min-width: 150px;
}

.notice-table tr:last-child td {
  border-bottom: none;
}

.notice-table td .join-button {
  margin: 0 5px;
  padding: 0.4rem 1rem;
  cursor: pointer;
  font-size: 0.9rem;
  border-radius: 20px;
  background-color: #a571c4;
  color: white;
  border: none;
}

.notice-table td .join-button img {
  width: 20px;
}

.join-btn {
  width: 185px;
}

.play-button {
  width: 50px;
}

.notice-item {
  margin: 20px 0px 20px 20px;
}

.department-info {
  margin: 0 auto;
}

.department-table {
  width: 100%;
  border-collapse: collapse;
  margin: 0 auto;
}

.department-table td {
  padding: 0.3rem 0.5rem;
}

.total-meeting-hours {
  text-align: center;
  margin-bottom: 1rem;
  font-weight: bold;
}

.total-meeting-hours p {
  margin-bottom: 15px;
}

.meeting-hours-bar {
  display: flex;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.meeting-hours-segment {
  height: 100%;
}

.prev-meetings {
  background-color: #e0dfdf;
}

.today-meetings {
  background-color: #d6b3f7;
}

.next-meetings {
  background-color: #f7b3d5;
}

.meeting-hours-legend {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  width: 70px;
  margin: 0 auto;
  margin-right: 5px;
}

.legend-item:last-child {
  margin-right: 0;
}

.legend-color {
  width: 10px;
  height: 10px;
  border-radius: 10px;
  margin-right: 5px;
}

.legend-color.prev-meetings {
  background-color: #e0dfdf;
}

.legend-color.today-meetings {
  background-color: #d6b3f7;
}

.legend-color.next-meetings {
  background-color: #f7b3d5;
}

.legend-label {
  font-size: 9px;
  font-weight: bold;
  width: 65px;
  margin: 0 auto;
}

.members {
  max-height: 100px;
  overflow-y: auto;
  margin-bottom: 1rem;
  padding-right: 10px;
  text-align: left;
  width: 100%;
  flex-direction: column;
  align-items: center;
}

.member {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  text-align: left;
  margin-bottom: 0.5rem;
  padding-left: 0.5rem;
  width: 100%;
}

.member img {
  border-radius: 50%;
  margin-right: 0.5rem;
}

.member p {
  margin: 0;
  padding-left: 5px;
}

.members-row {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin: 0 auto;
  text-align: center;
}

.add-member-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #808080;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  cursor: pointer;
  margin: 0 0 0 15px;
}

.invite-member-input {
  position: absolute;
  top: 100%;
  left: -40%;
  width: 180%;
  padding: 10px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}


.btns {
  border: none;
  text-align: center;
  cursor: pointer;
}

.btn-invite {
  padding: 5px 8px;
  font-size: 0.5rem;
  background-color: #6a1b9a; 
  border-radius: 3px; 
  color: white;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-invite:hover {
  background-color: #b380bc; /* hover 효과를 설정합니다. */
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
}

.btn-close:focus,
.btn-close:active {
  outline: none;
  border: none;
  box-shadow: none;
}

.invite-member-row {
  display: flex;
  align-items: center;
  width: 100%;
}

.search-member {
  /* flex: 1; */
  padding: 7.3px;
  border-radius: 4px;
  border: 1px solid #ddd;
  width: 100%;
  box-sizing: border-box;
}

.btn-invite {
  font-size: 0.6rem;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
  background-color: #6a1b9a;
  padding: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-invite:hover {
  background-color: #b380bc;
}
.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.3s ease;
}
.slide-fade-enter-from, .slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.members-dropdown {
  padding-top: 50pz;
}

.dropdown {
  list-style: none;
  padding: 0;
  margin: 0;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-height: 150px;
  overflow-y: auto;
  position: absolute;
  z-index: 1000;
  text-align: left;
}

.dropdown li {
  padding: 8px 12px;
  cursor: pointer;
}

.dropdown li:hover {
  background: #f5f5f5;
}

.notice-dropdown {
  width: 150px;
  left: 50%;
  transform: translateX(-50%);
}

.details-dropdown {
  width: 150px;
}

.before-dropdown {
  text-decoration: underline;
  font-size: medium;
  cursor: pointer;
}

.background-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 500;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 스크롤바 커스텀 */
.members::-webkit-scrollbar, .dropdown::-webkit-scrollbar {
  width: 12px;
}

.members::-webkit-scrollbar-thumb, .dropdown::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 5px;
}

.members::-webkit-scrollbar-thumb:hover, .dropdown::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.members::-webkit-scrollbar-track, .dropdown::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 5px;
}


@media (max-width: 992px) {
  .container {
    width: 90%;
    margin: auto;
  }

  .notice-content {
    max-height: 150px;
  }

  .intro-section {
    display: none;
  }

  .notice-table .meeting-name {
    width: 150px;
  }
}

.search-results {
  margin-top: 10px;
}

.search-result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0px;
  padding-left: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin: 0px;
  margin-bottom: 5px;
  background-color: #f9f9f9;
  transition: background-color 0.2s ease;
  box-sizing: border-box;
  height: 40px; /* 높이 통일 */
}

.search-result-item:hover {
  background-color: #f1f1f1;
}

.search-result-item span {
  flex-grow: 1; /* 이름이 버튼과 함께 한 줄에 있도록 설정 */
  font-size: 12px;
  font-weight: bold;
  line-height: 1.5;
}

.search-result-item .btn-invite {
  padding: 10px 15px; 
  border-radius: 3px;
  font-size: 10px;
  font-weight: bold;
  background-color: #6a1b9a;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
  box-sizing: border-box;
  height: 100%; /* 높이 통일 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-result-item .btn-invite:hover {
  background-color: #b380bc;
}
</style>
