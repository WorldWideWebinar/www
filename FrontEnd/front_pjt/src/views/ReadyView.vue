<template>
  <div v-if="isLoading" class="loading-container">
    <div class="spinner">
      <svg class="spinner-icon" viewBox="0 0 50 50">
        <circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
      </svg>
      <p>Loading...</p>
    </div>
  </div>
  <div v-else-if="!inConference" class="ready-page-container">
    <header class="header">
      <span>
        Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page
      </span>
    </header>

    <div v-if="showOverlay" class="background-overlay" @click="closeDropdowns"></div>
    <div class="sub-container">
      <TeamNotice />
      <main class="main-section">
        <section class="meeting-list-section">
          <div class="meeting-header">
            <h5 style="font-weight: bolder">🖥️ Meeting List</h5>
            <button v-if="isOwner" class="add-meeting-btn" @click="CreateMeeting">+</button>
          </div>
          <ul class="nav nav-tabs">
            <li class="nav-item" @click="selectTab('PREV')">
              <a :class="{ 'nav-link': true, active: activeTab == 'PREV' }" aria-current="page" href="#">PREV</a>
            </li>
            <li class="nav-item" @click="selectTab('TODAY')">
              <a :class="{ 'nav-link': true, active: activeTab == 'TODAY' }" aria-current="page" href="#">TODAY</a>
            </li>
            <li class="nav-item" @click="selectTab('NEXT')">
              <a :class="{ 'nav-link': true, active: activeTab == 'NEXT' }" aria-current="page" href="#">NEXT</a>
            </li>
          </ul>
          <!-- Meeting List -->
          <div>
            <table class="meeting-list">
              <thead>
                <tr>
                  <th>START</th>
                  <th>END</th>
                  <th>AGENDA</th>
                  <th>DURATION</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="meeting in filteredMeetings" :key="meeting.id">
                  <td>
                    <div class="meeting-date">{{ formatDateTime(meeting.start_at).date }}</div>
                    <div class="meeting-time">{{ formatDateTime(meeting.start_at).time }}</div>
                  </td>
                  <td>
                    <div class="meeting-date">{{ formatDateTime(meeting.end_at).date }}</div>
                    <div class="meeting-time">{{ formatDateTime(meeting.end_at).time }}</div>
                  </td>
                  <td @click="selectMeeting(meeting)">
                    <div>{{ meeting.name }}</div>
                  </td>
                  <td>
                    {{ calculateDuration(meeting.start_at, meeting.end_at) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Meeting Details -->
        <section :class="{ 'meeting-detail-section': true, 'hidden-detail-section': !selectedMeeting }">
          <template v-if="selectedMeeting">
            <div class="meeting-detail-header">
              <button @click="deleteMeeting()" v-if="new Date().getTime() < new Date(selectedMeeting?.start_at).getTime()&& isOwner">🗑️</button>
              <p :style="{ 'padding-left': (!isOwner || new Date().getTime() >= new Date(selectedMeeting?.start_at).getTime()) ? '25px' : '0' }">&nbsp;{{ selectedMeeting?.name }}&nbsp;</p>
              <button @click="closeMeetingDetails">X</button>
            </div>
            <div class="meeting-detail-content">
              <table class="meeting-detail-table">
                <tr>
                  <td><strong>Date</strong></td>
                  <td style="font-size: 0.75rem;">{{ formatDateRange(selectedMeeting?.start_at, selectedMeeting?.end_at) }}</td>
                </tr>
                <tr>
                  <td><strong>Time</strong></td>
                  <td>
                    {{ formatDateTime(selectedMeeting?.start_at).time }} - {{ formatDateTime(selectedMeeting?.end_at).time }}
                    <small>({{ calculateDuration(selectedMeeting?.start_at, selectedMeeting?.end_at) }})</small>
                  </td>
                </tr>
                <tr>
                  <td><strong>Details</strong></td>
                  <td>
                    <div>{{ selectedMeeting?.details }}</div>
                  </td>
                </tr>
                <!-- <tr>
                  <td><strong>Status</strong></td>
                  <td>
                    <button :class="buttonClass(detailType, selectedMeeting?.status)"
                      @click="toggleDetailStatus(selectedMeeting)">
                      {{ buttonText(detailType, selectedMeeting?.status) }}
                    </button>
                  </td>
                </tr>
                <tr>
                  <td><strong>Members</strong></td>
                  <td class="show-member before-dropdown" @click="toggleMembersList">
                    <ul v-show="showMembersList" class="detail-dropdown dropdown">
                      <li v-for="member in selectedMeetingMembers" :key="member.name" class="member">
                        {{ member.name }}
                      </li>
                    </ul>
                  </td>
                </tr> -->
                <tr>
                  <td><strong>Files</strong></td>
                  <td class="before-dropdown" @click="toggleFilesList">
                    <ul v-show="showFilesList" class="detail-dropdown dropdown">
                      <li v-for="file in selectedMeeting?.files" :key="file.name">
                        <a @click.prevent="previewFile(file)" href="#">{{ file.name }}</a> uploaded
                        by {{ file.uploader }}
                      </li>
                    </ul>
                  </td>
                </tr>
              </table>
              <div class="dash-separator"></div>
              <div class="files-section">
                <table class="files-table">
                  <thead>
                    <tr>
                      <td>
                        <a href="#" @click.prevent="downloadSummary" class="file-link">📂summary</a>
                      </td>
                      <td>
                        <a href="#" @click.prevent="
                          previewFile({ name: 'record.pdf', link: selectedMeeting.record })
                          " class="file-link">📁record</a>
                      </td>
                    </tr>
                  </thead>
                </table>
              </div>
              <div v-if="previewUrl" class="file-preview">
                <iframe :src="previewUrl" width="100%" height="400px"></iframe>
                <a :href="previewUrl" download>Download</a>
              </div>
            </div>
          </template>
        </section>
      </main>
    </div>
  </div>
  <router-view v-else></router-view>
  <MeetingCreate v-if="meetingCreateModal" @close="meetingCreateModal=false" @created="(teamId) => loadData(teamId)"/>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { handleClickOutside } from '@/utils.js';
import TeamNotice from '@/components/ReadyView/TeamNotice.vue';
import MeetingCreate from '@/components/MeetingCreateView/MeetingCreate.vue'
import router from '@/router';

const route = useRoute();
const teamStore = useTeamStore();
const userStore = useUserStore();
const meetingStore = useMeetingStore();
const inConference = ref(false);
const selectedMeeting = ref(null);
const detailType = ref('');
const showMembersList = ref(false);
const showFilesList = ref(false);
const showOverlay = ref(false);
const selectedMeetingMembers = ref([]);
const activeTab = ref('TODAY');
const previewUrl = ref(null);
const meetingCreateModal = ref(false);
const isLoading = ref(true);

const members = computed(() => teamStore.teamUserInfo);

const filteredMeetings = computed(() => {
  console.log(teamStore.groupedMeetings)
  if (activeTab.value === 'PREV') {
    return teamStore.groupedMeetings.PREV;
  } else if (activeTab.value === 'TODAY') {
    return teamStore.groupedMeetings.TODAY;
  } else if (activeTab.value === 'NEXT') {
    return teamStore.groupedMeetings.NEXT;
  }
  return [];
});

const departmentName = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.getTeamById(teamId);
  return teamData ? teamData.teamName : '';
});

const isOwner = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.getTeamById(teamId);
  return teamData && teamData.ownerId === userStore.userId;
});

const toggleStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const buttonClass = (status) => {
  return status === 'IN' ? 'btn-green' : 'btn-red';
};

const buttonText = (status) => status;

const deleteMeeting = async () =>{
  const meetingId = selectedMeeting.value.meeting_id;
  console.log(meetingId)
  try {
    const success = await meetingStore.deleteMeeting(meetingId);
    if(success){
      selectedMeeting.value = null;
      const teamId = route.params.id;
      teamStore.fetchMeetings(teamId, false, false) // TODAY
      teamStore.fetchMeetings(teamId, false, true) // NEXT
      teamStore.fetchMeetings(teamId, true, false), // PREV
    }
  } catch (error) {
    console.log(error)
  }
}

const toggleFilesList = () => {
  showFilesList.value = !showFilesList.value;
  showOverlay.value = showFilesList.value;
};

const toggleMembersList = () => {
  showMembersList.value = !showMembersList.value;
  showOverlay.value = showMembersList.value;
};

const previewFile = (file) => {
  previewUrl.value = file.link;
};

const downloadSummary = async () => {
  if (!selectedMeeting.value) {
    console.error('No meeting selected');
    return;
  }
  console.log(selectedMeeting.value)
  try {
    await meetingStore.downloadSummary(
        selectedMeeting.value.team_id, selectedMeeting.value.meeting_id);
  } catch (error) {
    console.error('Error handling summary download:', error);
  }
};

const closeDropdowns = () => {
  showFilesList.value = false;
  showMembersList.value = false;
  showOverlay.value = false;
};

const selectTab = async (tab) => {
  activeTab.value = tab;
};

const loadData = async () => {
  const teamId = route.params.id
  await teamStore.fetchTeamById(teamId)
  try {
    await Promise.all([      
      teamStore.fetchTeamUsers(),
      teamStore.fetchMeetings(teamId, false, false), // TODAY
      teamStore.fetchMeetings(teamId, false, true), // NEXT
      teamStore.fetchMeetings(teamId, true, false), // PREV
    ]);

    await selectTab('TODAY');
  } catch (error) {
    console.error('Failed to load data:', error);
  }
};

onMounted(async () => {
  isLoading.value = true;
  const teamId = parseInt(route.params.id, 10);

  await loadData(teamId);
  isLoading.value = false;
});

watch(() => route.params.id, async (newId) => {
  isLoading.value = true;
  teamStore.clearTeamMeetings();
  console.log(newId)
  await loadData(newId);
  isLoading.value = false;
});

const selectMeeting = (meeting) => {
  if (teamStore.groupedMeetings.PREV.includes(meeting)) {
    detailType.value = 'PREV';
  } else if (teamStore.groupedMeetings.TODAY.includes(meeting)) {
    detailType.value = 'TODAY';
  } else if (teamStore.groupedMeetings.NEXT.includes(meeting)) {
    detailType.value = 'NEXT';
  }
  selectedMeeting.value = meeting;
  console.log('Selected meeting:', selectedMeeting.value); // Debug log
  selectedMeetingMembers.value = members.value.slice(0, meeting.members);
  showMembersList.value = false;
  showFilesList.value = false;
  previewUrl.value = null;
  showOverlay.value = true;
};

const closeMeetingDetails = () => {
  selectedMeeting.value = null;
  selectedMeetingMembers.value = [];
  showMembersList.value = false;
  showFilesList.value = false;
  previewUrl.value = null;
  showOverlay.value = false;
};

const CreateMeeting = async () => {
  meetingCreateModal.value = true;
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside(selectedMeetingMembers, closeDropdowns));
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside(selectedMeetingMembers, closeDropdowns));
});

// 미팅 리스트 전용
const formatDateTime = (dateTimeString) => {
  const dateObj = new Date(dateTimeString);
  
  // 날짜 포맷: Aug 13rd, 2024
  const options = { month: 'short' };
  const month = dateObj.toLocaleDateString('en-US', options);
  const day = dateObj.getDate();
  const daySuffix = 
    day === 1 || day === 21 || day === 31 ? 'st' : 
    day === 2 || day === 22 ? 'nd' : 
    day === 3 || day === 23 ? 'rd' : 'th';
  const year = dateObj.getFullYear();
  const date = `${month} ${day}${daySuffix}, ${year}`;
  
  // 시간 포맷: 17:00
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const time = `${hours}:${minutes}`;
  
  return { date, time };
};

const calculateDuration = (startDateTime, endDateTime) => {
  const start = new Date(startDateTime);
  const end = new Date(endDateTime);
  const diffMs = end - start; // 차이 밀리초
  const diffMins = Math.floor(diffMs / 60000); // 차이 분
  
  const hours = Math.floor(diffMins / 60);
  const minutes = diffMins % 60;

  if (hours > 0 && minutes > 0) {
    return `${hours}h ${minutes}m`;
  } else if (hours > 0) {
    return `${hours}h`;
  } else {
    return `${minutes}m`;
  }
};

// 미팅 세부사항 전용
const formatDateRange = (startDateTime, endDateTime) => {
  const start = new Date(startDateTime);
  const end = new Date(endDateTime);

  const options = { month: 'short' };
  const startMonth = start.toLocaleDateString('en-US', options);
  const startDay = start.getDate();
  const startDaySuffix = 
    startDay === 1 || startDay === 21 || startDay === 31 ? 'st' : 
    startDay === 2 || startDay === 22 ? 'nd' : 
    startDay === 3 || startDay === 23 ? 'rd' : 'th';
  const startYear = start.getFullYear();

  const endMonth = end.toLocaleDateString('en-US', options);
  const endDay = end.getDate();
  const endDaySuffix = 
    endDay === 1 || endDay === 21 || endDay === 31 ? 'st' : 
    endDay === 2 || endDay === 22 ? 'nd' : 
    endDay === 3 || endDay === 23 ? 'rd' : 'th';
  const endYear = end.getFullYear();

  const startDate = `${startMonth} ${startDay}${startDaySuffix}`;
  const endDate = `${endMonth} ${endDay}${endDaySuffix}, ${endYear}`;

  if (start.toDateString() === end.toDateString()) {
    return `${startDate}, ${startYear}`;
  } else {
    return `${startDate} - ${endDate}`;
  }
};

</script>


<style scoped>
.ready-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 1rem;
  box-sizing: border-box;
  background-color: #fcf9fc;
}

.header {
  text-align: center;
  padding: 1rem 0 1.5rem 0;
  font-weight: bolder;
  font-size: xx-large;
}

.highlight {
  color: rgb(166, 125, 247);
}

.sub-container {
  width: 82%;
  box-sizing: border-box;
  padding: 0;
  margin: 0 auto;
}

.main-section {
  display: flex;
  justify-content: space-between;
  width: 100%;
  gap: 2rem;
  box-sizing: border-box;
  min-height: 400px;
  margin-top: 30px;
  /* padding: 0 1.7rem; */
  /* margin: 0 auto; */
  /* height: 385px; */
}

.meeting-list-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-sizing: border-box;
}

.meeting-detail-section {
  flex: 1.5;
  background-color: #ffffff;
  padding: 0 1rem;
  border-radius: 8px;
  box-sizing: border-box;
  border: 2px dashed rgb(232, 231, 234);
  width: 100%;
  height: auto;
}

.meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

ul.nav-tabs .nav-item .nav-link {
  color: black;
}

ul.nav-tabs .nav-item .nav-link:hover,
ul.nav-tabs .nav-item .nav-link.active {
  font-weight: bolder;
}

ul.nav {
  width: 100%;
}

ul.nav li {
  width: 33.33%;
  text-align: center;
}

.meeting-list {
  width: 102.5%;
  border-collapse: collapse;
  margin: 20px 0 0 0;
  font-size: 95%;
}

.meeting-list th,
.meeting-list td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: center;
}

.meeting-list th:nth-child(1),
.meeting-list td:nth-child(1) {
  width: 20%;
}

.meeting-list th:nth-child(2),
.meeting-list td:nth-child(2) {
  width: 20%;
}

.meeting-date {
  font-size: x-small;
}

.meeting-time {
  font-size: medium;
}

.meeting-list th:nth-child(3),
.meeting-list td:nth-child(3) {
  width: auto;
}

.meeting-list th:nth-child(4),
.meeting-list td:nth-child(4) {
  width: 20%;
}

.meeting-list td:nth-child(4):hover,
.meeting-list td:nth-child(4):hover *,
.meeting-list td.agenda:hover {
  font-weight: bold;
  cursor: pointer;
}

.meeting-list td:nth-child(3):hover,
.meeting-list td:nth-child(3):hover *,
.meeting-list td.agenda:hover {
  cursor: pointer;
  text-decoration-line: underline;
  text-decoration-style: line;
  text-decoration-color: rgba(154, 130, 253, 0.4);
  text-decoration-thickness: 3px;
}
/* 
.meeting-list .bold-agenda {
  font-weight: bolder;
  color: rgb(154, 130, 253);
} */

.meeting-list th {
  background-color: #f5f5f5;
  font-weight: bold;
}

td button {
  border: none;
  border-radius: 50px;
  width: 60px;
  padding: 5px;
  cursor: pointer;
  text-align: center;
}

button {
  border: none;
  cursor: pointer;
  text-align: center;
}

.btn-green {
  background-color: rgba(139, 195, 74, 0.5);
  color: black;
}

.btn-red {
  background-color: rgba(244, 67, 54, 0.5);
  color: black;
}

.btn-gray {
  background-color: rgba(108, 117, 125, 0.5);
  color: black;
}

.meeting-list tbody {
  display: block;
  max-height: 170px;
  overflow-y: scroll;
}

.meeting-list tbody::-webkit-scrollbar {
  width: 0;
  background: transparent;
}

.meeting-list tr {
  display: table;
  width: calc(100% - 1rem);
  table-layout: fixed;
}

.meeting-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  /* margin-left: 25px; */
}

.meeting-detail-header p {
  text-align: center;
  margin: 1rem auto;
  font-size: larger;
  font-weight: bolder;
  text-decoration-line: underline;
  text-decoration-style: line;
  text-decoration-color: rgba(154, 130, 253, 0.4);
  text-decoration-thickness: 5px;
}

.meeting-detail-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
  font-size: medium;
}

.meeting-detail-table td {
  padding: 0.3rem 0.5rem;
  font-size: medium;
}

.files-table {
  width: 100%;
  border-collapse: collapse;
  margin: 5px;
}

.files-table th,
.files-table td {
  padding: 0.3rem;
}

.file-link {
  display: inline-block;
  color: black;
  font-size: medium;
  text-decoration: none;
}

.dash-separator {
  border-top: 2px dashed #ccc;
  margin: 1.5rem 0 0.7rem 0;
}

.file-preview {
  margin-top: 10px;
  text-align: center;
}

.file-preview iframe {
  border: 1px solid #ccc;
  border-radius: 4px;
}

.before-dropdown {
  text-decoration: underline;
  font-size: medium;
  cursor: pointer;
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

.background-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 500;
}

.add-meeting-btn,
.chat-input button {
  background-color: #808080;
  border: none;
  border-radius: 8px;
  height: 30px;
  width: 33px;
  padding: 0 0.5rem;
  cursor: pointer;
  text-align: center;
}

.bold {
  font-weight: bold;
  margin: 0;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.spinner {
  text-align: center;
}

.spinner-icon {
  animation: spin 1s linear infinite;
  width: 50px;
  height: 50px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.spinner p {
  margin-top: 10px;
  font-size: 16px;
  color: #333;
}

@media (max-width: 992px) {
  .intro-section,
  .chat-section,
  .meeting-detail-section {
    display: none;
  }

  .sub-container {
    width: 100%;
  }

  .main-section {
    width: 90%;
    margin: 20px auto 0px auto;
  }
}


</style>
