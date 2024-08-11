<template>
  <div v-if="!inConference" class="ready-page-container">
    <header class="header">
      <span>
        Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page
      </span>
    </header>
    <div v-if="showOverlay" class="background-overlay" @click="closeDropdowns"></div>
    <div class="sub-container">
<<<<<<< HEAD
      <div class="top-section">
        <button @click="startConference" class="join-button">Start</button>
        <div class="notice-and-intro">
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
                  <p>{{ todayMeeting.start_at }} - {{ todayMeeting.end_at }}</p>
                  <p class="before-dropdown" @click="toggleTodayMembersList">
                    {{ todayMeeting.members.length }} members will join!
                  </p>
                  <ul v-show="showTodayMembersList" class="notice-dropdown dropdown">
                    <li v-for="member in todayMeetingMembers" :key="member.name" class="member">
                      <img :src="member.avatar" :alt="member.name" />{{ member.name }}
                    </li>
                  </ul>
                </div>
                <div class="notice-right">
                  <button v-if="isOwner" @click="startConference" class="join-button">Start</button>
                  <button v-else @click="joinConference" class="join-button">
                    <img class="play-button" src="../assets/img/playbutton.png" alt="play">
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
                <div class="meeting-hours-segment prev-meetings" :style="{ width: prevMeetingHoursPercentage + '%' }"
                  v-if="prevMeetingHours > 0"></div>
                <div class="meeting-hours-segment today-meetings" :style="{ width: todayMeetingHoursPercentage + '%' }"
                  v-if="todayMeetingHours > 0"></div>
                <div class="meeting-hours-segment next-meetings" :style="{ width: nextMeetingHoursPercentage + '%' }"
                  v-if="nextMeetingHours > 0"></div>
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
                          <img :src="member.avatar" :alt="member.name" />{{ member.name }}
                        </li>
                      </ul>
                    </td>
                  </tr>
                  <tr>
                    <td><strong>Created Date</strong></td>
                    <td>{{ departmentCreationDate }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>
=======
      <TeamNotice 
        :departmentName="departmentName" 
        :departmentCreationDate="departmentCreationDate"
        :isOwner="isOwner"
        :sessionId="sessionStore.sessionId"
      />
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
      <main class="main-section">
        <section class="meeting-list-section">
          <div class="meeting-header">
            <h5 style="font-weight: bolder">🖥️ Meeting List</h5>
            <button v-if="isOwner" class="add-meeting-btn" @click="CreateMeeting">+</button>
          </div>
<<<<<<< HEAD
          <ul class="nav nav-tabs">
            <li class="nav-item" @click="activeTab = 'PREV'">
              <a :class="{ 'nav-link': true, active: activeTab === 'PREV' }" aria-current="page" href="#">PREV</a>
            </li>
            <li class="nav-item" @click="activeTab = 'TODAY'">
              <a :class="{ 'nav-link': true, active: activeTab === 'TODAY' }" aria-current="page" href="#">TODAY</a>
            </li>
            <li class="nav-item" @click="activeTab = 'NEXT'">
              <a :class="{ 'nav-link': true, active: activeTab === 'NEXT' }" aria-current="page" href="#">NEXT</a>
            </li>
          </ul>

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
              <template v-if="activeTab === 'PREV'">
                <tr v-for="meeting in groupedMeetings.PREV" :key="meeting.meeting_id">
                  <td>{{ meeting.start_at.split('T')[0] }}</td>
                  <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
                  <td :class="{
                    agenda: true,
                    'bold-agenda':
                      selectedMeeting &&
                      selectedMeeting.meeting_id === meeting.meeting_id
                  }" @click="selectMeeting(meeting)">
                    {{ meeting.name }}
                  </td>
                  <td>
                    <button :class="buttonClass('PREV', meeting.status)" @click="toggleStatus(meeting)">
                      {{ buttonText('PREV', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'TODAY'">
                <tr v-for="meeting in groupedMeetings.TODAY" :key="meeting.meeting_id">
                  <td>{{ meeting.start_at.split('T')[0] }}</td>
                  <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
                  <td :class="{
                    agenda: true,
                    'bold-agenda':
                      selectedMeeting &&
                      selectedMeeting.meeting_id === meeting.meeting_id
                  }" @click="selectMeeting(meeting)">
                    {{ meeting.name }}
                  </td>
                  <td>
                    <button :class="buttonClass('TODAY', meeting.status)" @click="toggleStatus(meeting)">
                      {{ buttonText('TODAY', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'NEXT'">
                <tr v-for="meeting in groupedMeetings.NEXT" :key="meeting.meeting_id">
                  <td>{{ meeting.start_at.split('T')[0] }}</td>
                  <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
                  <td :class="{
                    agenda: true,
                    'bold-agenda':
                      selectedMeeting &&
                      selectedMeeting.meeting_id === meeting.meeting_id
                  }" @click="selectMeeting(meeting)">
                    {{ meeting.name }}
                  </td>
                  <td>
                    <button :class="buttonClass('NEXT', meeting.status)" @click="toggleStatus(meeting)">
                      {{ buttonText('NEXT', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
=======
          <MeetingList
            @update:activeTab="tab => activeTab = tab"
          />
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
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
                    <button :class="buttonClass(detailType, selectedMeeting?.status)"
                      @click="toggleDetailStatus(selectedMeeting)">
                      {{ buttonText(detailType, selectedMeeting?.status) }}
                    </button>
                  </td>
                </tr>
                <tr>
                  <td><strong>Members</strong></td>
                  <td class="show-member before-dropdown" @click="toggleMembersList">
<<<<<<< HEAD
                    {{ selectedMeeting?.members.length }} members joined!
                    <ul v-show="showMembersList" class="detail-dropdown dropdown">
                      <li v-for="member in selectedMeetingMembers" :key="member.name" class="member">
                        <img :src="member.avatar" :alt="member.name" />{{ member.name }}
=======
                    <!-- {{ selectedMeeting?.members.length }} members joined! -->
                    <ul v-show="showMembersList" class="detail-dropdown dropdown">
                      <li v-for="member in selectedMeetingMembers" :key="member.name" class="member">
                        <!-- <img :src="member.avatar" :alt="member.name" /> -->
                        {{ member.name }}
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
                      </li>
                    </ul>
                  </td>
                </tr>
                <tr>
                  <td><strong>Files</strong></td>
                  <td class="before-dropdown" @click="toggleFilesList">
                    <!-- {{ selectedMeeting?.files.length }} files uploaded -->
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
                        <a href="#" @click.prevent="
                          previewFile({ name: 'summary.pdf', link: selectedMeeting.summary })
                          " class="file-link">📂summary</a>
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
  <MeetingCreate v-if="meetingCreateModal" @close="meetingCreateModal=false" :propedTeamId="propTeamId"/>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { useSessionStore } from '@/stores/sessionStore';
<<<<<<< HEAD
import axios from 'axios';
import MeetingCreate from '@/components/MeetingCreateView/MeetingCreate.vue';
=======
import MeetingCreate from '@/components/MeetingCreateView/MeetingCreate.vue';
import MeetingList from '@/components/ReadyView/MeetingList.vue';
import TeamNotice from '@/components/ReadyView/TeamNotice.vue';
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c

const route = useRoute();
const router = useRouter();
const teamStore = useTeamStore();
const userStore = useUserStore();
const meetingStore = useMeetingStore();
const sessionStore = useSessionStore();
const inConference = ref(false);
const selectedMeeting = ref(null);
const detailType = ref('');
const showMembersList = ref(false);
const showFilesList = ref(false);
const showMemberListDropdown = ref(false);
const showOverlay = ref(false);
const todayMeetingMembers = ref([]);
const selectedMeetingMembers = ref([]);
const activeTab = ref('TODAY');
const departmentCreationDate = ref('2022-01-01');
const meetingCreateModal = ref(false);
const propTeamId = ref('');
<<<<<<< HEAD

const members = ref([
  { name: 'Robert', avatar: 'https://via.placeholder.com/32' },
  { name: 'Lisa', avatar: 'https://via.placeholder.com/32' },
  { name: 'Tom', avatar: 'https://via.placeholder.com/32' },
  { name: 'Mike', avatar: 'https://via.placeholder.com/32' },
  { name: 'Sophie', avatar: 'https://via.placeholder.com/32' },
  { name: 'Rachael', avatar: 'https://via.placeholder.com/32' }
]);

const route = useRoute();
const router = useRouter();
const teamStore = useTeamStore();
const userStore = useUserStore();
const meetingStore = useMeetingStore();
const sessionStore = useSessionStore();

const meetings = computed(() => meetingStore.meetings);
=======
const previewUrl = ref(null);

const members = computed(() => teamStore.teamUserInfo);
const meetings = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  return meetingStore.meetings.filter(meeting => meeting.team_id === teamId);
});
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c

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

const todayMeeting = computed(() => {
  const today = new Date().toISOString().split('T')[0];
  return meetings.value.find((meeting) => meeting.start_at.split('T')[0] === today);
<<<<<<< HEAD
});

const groupedMeetings = computed(() => {
  const groups = {
    PREV: [],
    TODAY: [],
    NEXT: []
  };
  const now = new Date();
  const sortedMeetings = [...meetings.value].sort((a, b) => new Date(b.start_at) - new Date(a.start_at));
  sortedMeetings.forEach((meeting) => {
    const startDateTime = new Date(meeting.start_at);
    const endDateTime = new Date(meeting.end_at);
    if (endDateTime < now) {
      groups.PREV.push(meeting);
    } else if (startDateTime <= now && endDateTime >= now) {
      groups.TODAY.push(meeting);
    } else if (startDateTime > now) {
      groups.NEXT.push(meeting);
    }
  });
  return { NEXT: groups.NEXT, TODAY: groups.TODAY, PREV: groups.PREV };
});

const totalMeetingHours = computed(() => {
  return meetings.value.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const prevMeetingHours = computed(() => {
  return groupedMeetings.value.PREV.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const todayMeetingHours = computed(() => {
  return groupedMeetings.value.TODAY.reduce((total, meeting) => {
    const start = new Date(meeting.start_at);
    const end = new Date(meeting.end_at);
    return total + (end - start) / (1000 * 60 * 60); // 밀리초를 시간으로 변환
  }, 0);
});

const nextMeetingHours = computed(() => {
  return groupedMeetings.value.NEXT.reduce((total, meeting) => {
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

const joinConference = async () => {
  try {
    const response = await axios.get('http://localhost:5000/api/sessions');
    sessionId.value = response.data.sessionId;

    router
      .push({ name: 'ConferenceView', params: { sessionId: sessionId.value } })
      .then(() => {
        inConference.value = true;
      })
      .catch((err) => {
        console.error('Error navigating to ConferenceView:', err);
      });
  } catch (error) {
    console.error('Failed to join conference:', error);
  }
};

const selectMeeting = (meeting) => {
  selectedMeeting.value = meeting;
  detailType.value = computeDetailType(meeting.start_at);
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

=======
});

>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
const toggleStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const buttonClass = (type, status) => {
  if (type === 'NEXT') return status === 'IN' ? 'btn-green' : 'btn-red';
  if (type === 'PREV') return 'btn-gray';
  if (type === 'TODAY') return status === 'IN' ? 'btn-green' : 'btn-red';
  return '';
};

<<<<<<< HEAD
const buttonText = (type, status) => {
  return status;
};

const computeDetailType = (start_at) => {
  const today = new Date().toISOString().split('T')[0];
  if (start_at.split('T')[0] === today) return 'TODAY';
  else if (start_at.split('T')[0] > today) return 'NEXT';
  else return 'PREV';
};

const toggleMemberListDropdown = () => {
  showMemberListDropdown.value = !showMemberListDropdown.value;
  showOverlay.value = showMemberListDropdown.value;
};

const toggleTodayMembersList = () => {
  showTodayMembersList.value = !showTodayMembersList.value;
  if (todayMeeting.value) {
    todayMeetingMembers.value = members.value.slice(0, todayMeeting.value.members);
  }
  showOverlay.value = showTodayMembersList.value;
};

const selectLatestTodayMeeting = () => {
  const todayMeetings = groupedMeetings.value.TODAY;
  if (todayMeetings.length > 0) {
    selectMeeting(todayMeetings[0]);
  }
};
=======
const buttonText = (type, status) => status;
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c

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

const closeDropdowns = () => {
  showMemberListDropdown.value = false;
  showFilesList.value = false;
  showMembersList.value = false;
  showOverlay.value = false;
};

onMounted(async () => {
  const teamId = parseInt(route.params.id, 10);
<<<<<<< HEAD
  await teamStore.fetchTeamById(teamId);
  selectLatestTodayMeeting();
});

watch(
  () => route.params.id,
  async (newId) => {
    const teamId = parseInt(newId, 10);
    propTeamId.value = teamId;
    await teamStore.fetchTeamById(teamId);
  }
);
=======
  try {
    await teamStore.fetchTeamById(teamId);
    await teamStore.fetchTeamUsers();
  } catch (error) {
    console.error('Failed to load initial data:', error);
  }
});

const selectMeeting = (meeting) => {
  selectedMeeting.value = meeting;
  detailType.value = computeDetailType(meeting.start_at);
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

const computeDetailType = (start_at) => {
  const today = new Date().toISOString().split('T')[0];
  if (start_at.split('T')[0] === today) return 'TODAY';
  if (start_at.split('T')[0] > today) return 'NEXT';
  return 'PREV';
};

watch(() => route.params.id, async (newId) => {
  const teamId = parseInt(newId, 10);
  propTeamId.value = teamId;
  await teamStore.fetchTeamById(teamId);
});
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c

watch(activeTab, (newTab) => {
  if (newTab === 'TODAY') {
    // 여기에 추가 작업이 필요할 경우 작성
  }
});

const CreateMeeting = () => {
  meetingCreateModal.value = true;
<<<<<<< HEAD
};

const startConference = async () => {
  const meetingId = selectedMeeting.value.meeting_id; 
  const userId = userStore.userId; 
  await sessionStore.startConference(meetingId, userId);
};
</script>


=======
};
</script>
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c

<style scoped>
.ready-page-container {
  display: flex;
  flex-direction: column;
  min-height: 110vh;
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
  margin: 0 auto;
}

.main-section {
  display: flex;
  padding: 1rem;
  justify-content: space-between;
  width: 100%;
  gap: 2rem;
  box-sizing: border-box;
  height: 385px;
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

.meeting-list .bold-agenda {
  font-weight: bolder;
  color: rgb(154, 130, 253);
}

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
  max-height: 140px;
<<<<<<< HEAD
  /* 원하는 최대 높이 설정 */
=======
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
  overflow-y: scroll;
}

.meeting-list tbody::-webkit-scrollbar {
  width: 0;
<<<<<<< HEAD
  /* 스크롤바의 너비를 0으로 설정 */
  background: transparent;
  /* 스크롤바 배경을 투명하게 설정 */
=======
  background: transparent;
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
}

.meeting-list tr {
  display: table;
  width: calc(100% - 1rem);
<<<<<<< HEAD
  /* 테이블 너비를 100%에서 약간 줄임 */
=======
>>>>>>> f8d0c8f7e0860c78f61a013f2540a96c4c52682c
  table-layout: fixed;
}

.meeting-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-left: 25px;
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

@media (max-width: 992px) {

  .intro-section,
  .chat-section,
  .meeting-detail-section {
    display: none;
  }

  .sub-container {
    width: 100%;
  }
}
</style>
