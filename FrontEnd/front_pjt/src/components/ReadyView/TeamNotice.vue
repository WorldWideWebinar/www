<template>
  <div class="container">
    <section class="notice-section">
      <div class="notice-header">
        <h5 style="font-weight: bolder"><span class="icon">🏴</span> Notice</h5>
      </div>
      <div class="notice-content">
        <div v-if="todayMeeting" class="notice-item">
          <div class="notice-left">
            <p class="bold">{{ todayMeeting.name }}</p>
          </div>
          <div class="notice-middle">
            <p>{{ formatDate(todayMeeting.start_at) }} {{ formatTime(todayMeeting.start_at) }} - {{ formatDate(todayMeeting.end_at) }} {{ formatTime(todayMeeting.end_at) }}</p>
            <p class="before-dropdown" @click="toggleTodayMembersList">
              <!-- {{ todayMeeting.members.length }} members will join! -->
            </p>
            <ul v-show="showTodayMembersList" class="notice-dropdown dropdown">
              <li v-for="member in members" :key="member.name" class="member">
                {{ member.name }}
              </li>
            </ul>
          </div>
          <div class="notice-right">
            <button @click="handleStartConference(todayMeeting.meeting_id, todayMeeting.name)" class="join-button">Start</button>
            <button @click="handleJoinConference(todayMeeting.name)" class="join-button">
              <img class="play-button" src="@/assets/img/playbutton.png" alt="play">
            </button>
          </div>
        </div>
        <div v-else class="notice-item">
          <p class="no-meeting">There's no meeting today :)</p>
        </div>
      </div>
    </section>
    <section class="intro-section">
      <div class="total-meeting-hours">
        <p>We have meetings for {{ totalMeetingHours }} hours</p>
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
              <td>{{ departmentName }}</td>
            </tr>
            <tr>
              <td><strong>Members</strong></td>
              <td>
                <div class="members-row" @click="toggleMemberListDropdown">
                  {{ members.length }} members
                  <button class="add-member-btn">+</button>
                </div>
                <ul v-show="showMemberListDropdown" class="members-dropdown dropdown">
                  <li v-for="member in members" :key="member.name" class="member">
                    {{ member.name }}
                  </li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { useSessionStore } from '@/stores/sessionStore';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';
const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const sessionStore = useSessionStore();
const userStore = useUserStore();
const router = useRouter();

const showTodayMembersList = ref(false);
const showMemberListDropdown = ref(false);

const members = computed(() => teamStore.teamUserInfo);
const todayMeeting = computed(() => {
  const today = new Date().toISOString().split('T')[0];
  return meetingStore.meetings.find((meeting) => meeting.start_at.split('T')[0] === today);
});

const totalMeetingHours = computed(() => {
  return meetingStore.meetings.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const prevMeetingHours = computed(() => {
  return meetingStore.groupedMeetings.PREV.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const todayMeetingHours = computed(() => {
  return meetingStore.groupedMeetings.TODAY.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const nextMeetingHours = computed(() => {
  return meetingStore.groupedMeetings.NEXT.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const prevMeetingHoursPercentage = computed(() => {
  return (prevMeetingHours.value / totalMeetingHours.value) * 100;
});

const todayMeetingHoursPercentage = computed(() => {
  return (todayMeetingHours.value / totalMeetingHours.value) * 100;
});

const nextMeetingHoursPercentage = computed(() => {
  return (nextMeetingHours.value / totalMeetingHours.value) * 100;
});

const departmentName = computed(() => teamStore.currentTeam?.teamName || '');
const isOwner = computed(() => teamStore.currentTeam?.ownerId === userStore.userId);

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

const toggleTodayMembersList = () => {
  showTodayMembersList.value = !showTodayMembersList.value;
};

const toggleMemberListDropdown = () => {
  showMemberListDropdown.value = !showMemberListDropdown.value;
};

const handleStartConference = async (meetingId, sessionName) => {
  const userId = userStore.userId;
  try {
    let sessionId = sessionStore.sessionId; // 이미 저장된 sessionId 확인

    if (!sessionId) {
      // sessionId가 없는 경우 새로운 세션 시작
      sessionId = await sessionStore.startConference(meetingId, userId, sessionName);
    }

    const token = await sessionStore.joinConference(sessionId);
    router.push({ name: 'ConferenceView', params: { sessionId, token} });
  } catch (error) {
    console.error('Failed to start conference:', error);
  }
};

const handleJoinConference = async (sessionName) => {
  console.log(isOwner)
  try {
    const token = await sessionStore.joinConference(sessionName);
    router.push({ name: 'ConferenceView', params: { sessionId: sessionName, token } });
  } catch (error) {
    console.error('Failed to join conference:', error);
  }
};
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
  margin-bottom: 1rem;
}

.notice-header h5 .icon {
  margin-right: 5px;
}

.notice-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px 0;
  background-color: #f9f9f9;
}

.notice-item {
  flex: 1;
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-right: 1rem;
}

.no-meeting {
  margin: auto;
}

.notice-left,
.notice-middle,
.notice-right {
  flex: 1;
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
</style>