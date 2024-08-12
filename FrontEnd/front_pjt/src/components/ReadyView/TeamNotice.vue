<template>
  <div class="container">
    <section class="notice-section">
      <div class="notice-header">
        <h5 style="font-weight: bolder"><span class="icon">🏴</span> Notice</h5>
        <button @click="checkTodayMeeting(todayMeeting)"></button>
      </div>
      <div class="notice-content">
        <table v-if="todayMeetings.length > 0" class="notice-table">
          <tr v-for="meeting in todayMeetings" :key="meeting.id">
            <td>{{ formatTime(meeting.start_at) }} - {{ formatTime(meeting.end_at) }}</td>
            <td class="bold">{{ meeting.name }}</td>
            <td>
              <button @click="handleStartConference(meeting.id, meeting.name)" class="join-button">Start</button>
              <button @click="handleJoinConference(meeting.name)" class="join-button">
                <img class="play-button" src="@/assets/img/play.png" alt="play">
              </button>
            </td>
          </tr>
        </table>
        <div v-else class="notice-item">
          <p class="no-meeting">There's no meeting today :)</p>
        </div>
      </div>
    </section>
    <section class="intro-section">
      <div class="total-meeting-hours">
        <p>We have {{ totalParticipants }} members and meetings for {{ totalMeetingHours.toFixed(2) }} hours</p>
        <div class="meeting-hours-bar">
          <div class="meeting-hours-segment prev-meetings" :style="{ width: prevMeetingHoursPercentage + '%' }" v-if="prevMeetingHours > 0"></div>
          <div class="meeting-hours-segment today-meetings" :style="{ width: todayMeetingHoursPercentage + '%' }" v-if="todayMeetingHours > 0"></div>
          <div class="meeting-hours-segment next-meetings" :style="{ width: nextMeetingHoursPercentage + '%' }" v-if="nextMeetingHours > 0"></div>
        </div>
        <div class="meeting-hours-legend">
          <div class="legend-item">
            <span class="legend-color prev-meetings"></span>
            <span class="legend-label">Previous {{ prevMeetingHours }}</span>
          </div>
          <div class="legend-item">
            <span class="legend-color today-meetings"></span>
            <span class="legend-label">Today {{ todayMeetingHours }}</span>
          </div>
          <div class="legend-item">
            <span class="legend-color next-meetings"></span>
            <span class="legend-label">Next {{ nextMeetingHours }}</span>
          </div>
        </div>
      </div>
      <div class="department-info">
        <table class="department-table">
          <tbody>
            <tr>
              <td><strong>Name</strong></td>
              <td style="position: relative;">
                <div class="members-row" @click="toggleMemberListDropdown" ref="memberDropdown">
                  {{ members.length }} members
                  <button class="add-member-btn" @click.stop="toggleInviteMemberInput">+</button>
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
                      <input class="search-member" v-model="newMemberId" placeholder="Enter member ID" />
                      <button @click="inviteMember" class="btns btn-invite">Invite</button>
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
import { useUserStore } from '@/stores/userStore';
import { useRouter, useRoute } from 'vue-router'
import { useSessionStore } from '@/stores/sessionStore'

const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const todayMeetings = computed(() => meetingStore.groupedMeetings.TODAY || []);

const teamId = computed(() => teamStore.teamInfo?.id);

const prevMeetingHours = computed(() => teamId.value ? teamStore.prevMeetingHoursByTeam(teamId.value) : 0);
const todayMeetingHours = computed(() => teamId.value ? teamStore.todayMeetingHoursByTeam(teamId.value) : 0);
const nextMeetingHours = computed(() => teamId.value ? teamStore.nextMeetingHoursByTeam(teamId.value) : 0);
const totalMeetingHours = computed(() => prevMeetingHours.value + todayMeetingHours.value + nextMeetingHours.value);

const totalParticipants = computed(() => teamId.value ? teamStore.totalParticipantsByTeam(teamId.value) : 0);

const showMemberListDropdown = ref(false);
const showInviteMemberInput = ref(false);
const newMemberId = ref('');
const members = computed(() => teamStore.teamUserInfo);
const userStore = useUserStore();
const sessionStore = useSessionStore();
const router = useRouter();
const route = useRoute()

const todayMeeting = computed(() => {
  const teamId = parseInt(route.params.id, 10); 
  return meetingStore.groupedMeetings.TODAY.find(meeting => meeting.team_id === teamId);
});

const totalMeetingHours = computed(() => {
  if (!meetingStore.meetings.length) return 0;
  return meetingStore.meetings.reduce((total, meeting) => {
    const start = new Date(meeting.start);
    const end = new Date(meeting.end);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const prevMeetingHours = computed(() => {
  if (!meetingStore.groupedMeetings.PREV.length) return 0;
  return meetingStore.groupedMeetings.PREV.reduce((total, meeting) => {
    const start_at = new Date(meeting.start_at);
    const end_at = new Date(meeting.end_at);
    return total + (end_at - start_at) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const todayMeetingHours = computed(() => {
  if (!meetingStore.groupedMeetings.TODAY.length) return 0;
  return meetingStore.groupedMeetings.TODAY.reduce((total, meeting) => {
    const start_at = new Date(meeting.start_at);
    const end_at = new Date(meeting.end_at);
    return total + (end_at - start_at) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const nextMeetingHours = computed(() => {
  if (!meetingStore.groupedMeetings.NEXT.length) return 0;
  return meetingStore.groupedMeetings.NEXT.reduce((total, meeting) => {
    const start = new Date(meeting.start);
    const end = new Date(meeting.end);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const prevMeetingHoursPercentage = computed(() => {
  if (totalMeetingHours.value === 0) return 0;
  return (prevMeetingHours.value / totalMeetingHours.value) * 100;
});

const todayMeetingHoursPercentage = computed(() => {
  if (totalMeetingHours.value === 0) return 0;
  return (todayMeetingHours.value / totalMeetingHours.value) * 100;
});

const nextMeetingHoursPercentage = computed(() => {
  if (totalMeetingHours.value === 0) return 0;
  return (nextMeetingHours.value / totalMeetingHours.value) * 100;
});

const departmentName = computed(() => teamStore.currentTeam?.teamName || '');
const isOwner = computed(() => {
  const teamId = parseInt(route.params.id, 10); // route.params.id를 정수로 변환
  const teamData = teamStore.teams.find(team => team.id === teamId); // teamId에 해당하는 팀을 찾음
  
  return teamData ? teamData.ownerId === userStore.userId : false; // 팀의 ownerId와 현재 user의 userId 비교
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${month}-${day}`;
};

const formatTime = (dateString) => {
  const date = new Date(dateString);
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
};

// const toggleTodayMembersList = () => {
//   showTodayMembersList.value = !showTodayMembersList.value;
// };

const toggleMemberListDropdown = () => {
  showMemberListDropdown.value = !showMemberListDropdown.value;
};

const toggleInviteMemberInput = () => {
  showInviteMemberInput.value = !showInviteMemberInput.value;
};

const handleStartConference = async (meetingId) => {
  const userId = userStore.userId;
  console.log(meetingId)
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

const inviteMember = async () => {
  if (newMemberId.value) {
    try {
      // 초대 멤버 로직
      showInviteMemberInput.value = false;
      newMemberId.value = '';
    } catch (error) {
      console.error('Failed to invite member:', error);
    }
  }
};

const cancelInvite = () => {
  showInviteMemberInput.value = false;
  newMemberId.value = '';
};

const closeMemberListDropdown = () => {
  showMemberListDropdown.value = false;
};

const handleClickOutside = (event) => {
  const inviteInputElement = document.querySelector('.invite-member-input');
  const memberDropdownElement = document.querySelector('.members-dropdown');
  
  // 초대 입력 창 외부 클릭 시 닫기
  if (inviteInputElement && !inviteInputElement.contains(event.target)) {
    cancelInvite();
  }

  // 멤버 리스트 드롭다운 외부 클릭 시 닫기
  if (memberDropdownElement && !memberDropdownElement.contains(event.target) && !event.target.closest('.members-row')) {
    closeMemberListDropdown();
  }
};

const checkTodayMeeting = () => {
  console.log("todayMeeting: ", todayMeeting)
  console.log("group meetings" , meetingStore.groupedMeetings)
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.container {
  display: flex;
  justify-content: space-between;
  gap: 2rem;
  width: 100%;
  border-radius: 8px 8px 0 0;
}

.notice-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
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
}

.notice-header,
.meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.notice-header h5 .icon {
  margin-right: 5px;
}

.notice-content {
  max-height: 100px; /* 원하는 최대 높이 설정 */
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 0px 10px;
  background-color: #f9f9f9;
}

.notice-item {
  flex: 1;
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-right: 1rem;
}

.notice-table th,
.notice-table td {
  padding: 5px;
  text-align: center;
  position: relative;
  margin: 5px;
}

.notice-middle {
  flex: 2;
}

.notice-right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.notice-left::after,
.notice-middle::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  border-right: 1px dashed #ccc;
  transform: translateX(50%);
}

.notice-right::after {
  display: none;
}

.notice-right button {
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  padding: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
  border-radius: 100px;
  background-color: none;
}

.notice-right {
  font-size: 1.2rem;
  padding: 5px;
}

.play-button {
  width: 50px;
}

.department-table {
  width: 100%;
  border-collapse: collapse;
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
  margin-right: 15px;
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
  left: 0;
  width: 100%;
  padding: 10px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.btns {
  border: none;
  text-align: center;
  cursor: pointer;
}

.btn-close {
  /* background-color: #6c757d; */
  color: #5a6268;
  font-size: xx-small;
  position: absolute;
  top: 0;
  right: 0;
  padding: 2px;
  outline: none;
  background: none;
  border: none;
  cursor: pointer;
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
  padding: 8px;
  border-radius: 4px 0 0 4px;
  border: 1px solid #ddd;
  width: 100%;
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
}
</style>