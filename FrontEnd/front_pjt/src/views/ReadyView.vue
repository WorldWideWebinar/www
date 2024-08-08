<template>
  <div v-if="!inConference" class="ready-page-container">
    <header class="header">
      <span>
        Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page
      </span>
    </header>
    <TeamNotice />

    <div class="sub-container">
      <main class="main-section">
        <section class="meeting-list-section">
          <div class="meeting-header">
            <h5 style="font-weight: bolder">🖥️ Meeting List</h5>
            <button v-if="isOwner" class="add-meeting-btn" @click="CreateMeeting">+</button>
          </div>
          <ul class="nav nav-tabs">
            <li class="nav-item" @click="selectTab('PREV')">
              <a :class="{ 'nav-link': true, active: activeTab === 'PREV' }" aria-current="page" href="#">PREV</a>
            </li>
            <li class="nav-item" @click="selectTab('TODAY')">
              <a :class="{ 'nav-link': true, active: activeTab === 'TODAY' }" aria-current="page" href="#">TODAY</a>
            </li>
            <li class="nav-item" @click="selectTab('NEXT')">
              <a :class="{ 'nav-link': true, active: activeTab === 'NEXT' }" aria-current="page" href="#">NEXT</a>
            </li>
          </ul>
          <div>
            <table class="meeting-list">
              <thead>
                <tr>
                  <th>DATE</th>
                  <th>TIME</th>
                  <th>AGENDA</th>
                  <th>JOIN</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="meeting in filteredMeetings" :key="meeting.id">
                  <td>{{ meeting.start_at.split('T')[0] }}</td>
                  <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
                  <td :class="{ agenda: true, 'bold-agenda': selectedMeeting && selectedMeeting.id === meeting.id }"
                    @click="selectMeeting(meeting)">
                    {{ meeting.name }}
                  </td>
                  <td>
                    <button :class="buttonClass(meeting.status)" @click="toggleStatus(meeting)">
                      {{ buttonText(meeting.status) }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section :class="{ 'meeting-detail-section': true, 'hidden-detail-section': !selectedMeeting }">
          <template v-if="selectedMeeting">
            <div class="meeting-detail-header">
              <p>&nbsp;{{ selectedMeeting?.name }}&nbsp;</p>
              <button @click="closeMeetingDetails">X</button>
            </div>
            <div class="meeting-detail-content">
              <table class="meeting-detail-table">
                <tr>
                  <td><strong>Date</strong></td>
                  <td>{{ selectedMeeting?.start_at.split('T')[0] }}</td>
                </tr>
                <tr>
                  <td><strong>Time</strong></td>
                  <td>{{ selectedMeeting?.start_at.split('T')[1] }} - {{ selectedMeeting?.end_at.split('T')[1] }}</td>
                </tr>
                <tr>
                  <td><strong>Status</strong></td>
                  <td>
                    <button :class="buttonClass(selectedMeeting?.status)" @click="toggleStatus(selectedMeeting)">
                      {{ buttonText(selectedMeeting?.status) }}
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
                </tr>
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
                        <a href="#" @click.prevent="previewFile({ name: 'summary.pdf', link: selectedMeeting.summary })"
                          class="file-link">📂summary</a>
                      </td>
                      <td>
                        <a href="#" @click.prevent="previewFile({ name: 'record.pdf', link: selectedMeeting.record })"
                          class="file-link">📁record</a>
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
  <MeetingCreate v-if="meetingCreateModal" @close="meetingCreateModal=false" />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { useSessionStore } from '@/stores/sessionStore';
import MeetingCreate from '@/components/MeetingCreateView/MeetingCreate.vue';
import TeamNotice from '@/components/ReadyView/TeamNotice.vue';

const route = useRoute();
const router = useRouter();
const teamStore = useTeamStore();
const userStore = useUserStore();
const meetingStore = useMeetingStore();
const sessionStore = useSessionStore();
const selectedMeeting = ref(null);
const showMembersList = ref(false);
const showFilesList = ref(false);
const selectedMeetingMembers = ref([]);
const activeTab = ref('TODAY');
const previewUrl = ref(null);
const meetingCreateModal = ref(false);
const inConference = ref(false);

const members = computed(() => teamStore.teamUserInfo);

const departmentName = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.teams.find((team) => team.id === teamId);
  return teamData ? teamData.teamName : '';
});

const isOwner = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.teams.find((team) => team.id === teamId);
  return teamData && teamData.ownerId == userStore.userId;
});

const filteredMeetings = computed(() => {
  const now = new Date();
  if (activeTab.value === 'PREV') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.end_at) < now);
  } else if (activeTab.value === 'TODAY') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.start_at) <= now && new Date(meeting.end_at) >= now);
  } else if (activeTab.value === 'NEXT') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.start_at) > now);
  }
  return [];
});

const toggleStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const buttonClass = (status) => {
  return status === 'IN' ? 'btn-green' : 'btn-red';
};

const buttonText = (status) => status;

const toggleFilesList = () => {
  showFilesList.value = !showFilesList.value;
};

const toggleMembersList = () => {
  showMembersList.value = !showMembersList.value;
};

const previewFile = (file) => {
  previewUrl.value = file.link;
};

const selectTab = async (tab) => {
  activeTab.value = tab;
  const teamId = parseInt(route.params.id, 10);
  const prev = tab === 'PREV' ? 1 : 0;
  const next = tab === 'NEXT' ? 1 : 0;
  await meetingStore.fetchMeetings(teamId, prev, next);
};

onMounted(async () => {
  const teamId = parseInt(route.params.id, 10);
  try {
    await teamStore.fetchTeamById(teamId);
    await teamStore.fetchTeamUsers();
  } catch (error) {
    console.error('Failed to load initial data:', error);
  }
  selectTab('TODAY');
});

const selectMeeting = (meeting) => {
  selectedMeeting.value = meeting;
  selectedMeetingMembers.value = members.value.slice(0, meeting.members);
  showMembersList.value = false;
  showFilesList.value = false;
  previewUrl.value = null;
};

const closeMeetingDetails = () => {
  selectedMeeting.value = null;
  selectedMeetingMembers.value = [];
  showMembersList.value = false;
  showFilesList.value = false;
  previewUrl.value = null;
};

const CreateMeeting = () => {
  meetingCreateModal.value = true;
};
</script>

<style scoped>


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
  background-color: none;
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