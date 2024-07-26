<template>
  <div v-if="!inConference" class="ready-page-container">
    <header class="header">
      <span>
        Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page
      </span>
    </header>
    <div v-if="showOverlay" class="background-overlay" @click="closeDropdowns"></div>
    <div class="sub-container">
      <div class="top-section">
        <div class="notice-and-intro">
          <section class="notice-section">
            <div class="notice-header">
              <h5 style="font-weight: bolder"><span class="icon">🏴</span> Notice</h5>
            </div>
            <div class="notice-content">
              <div v-if="todayMeeting" class="notice-item">
                <div class="notice-left">
                  <p class="bold">{{ todayMeeting.agenda }}</p>
                </div>
                <div class="notice-middle">
                  <p>{{ todayMeeting.time }}</p>
                  <p class="before-dropdown" @click="toggleTodayMembersList">
                    {{ todayMeeting.members }} members will join!
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
                <div
                  class="meeting-hours-segment prev-meetings"
                  :style="{ width: prevMeetingHoursPercentage + '%' }"
                  v-if="prevMeetingHours > 0"
                ></div>
                <div
                  class="meeting-hours-segment today-meetings"
                  :style="{ width: todayMeetingHoursPercentage + '%' }"
                  v-if="todayMeetingHours > 0"
                ></div>
                <div
                  class="meeting-hours-segment next-meetings"
                  :style="{ width: nextMeetingHoursPercentage + '%' }"
                  v-if="nextMeetingHours > 0"
                ></div>
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
      <main class="main-section">
        <section class="meeting-list-section">
          <div class="meeting-header">
            <h5 style="font-weight: bolder">🖥️ Meeting List</h5>
            <button v-if="isOwner" class="add-meeting-btn" @click="CreateMeeting">+</button>
          </div>
          <ul class="nav nav-tabs">
            <li class="nav-item" @click="activeTab = 'PREV'">
              <a
                :class="{ 'nav-link': true, active: activeTab === 'PREV' }"
                aria-current="page"
                href="#"
                >PREV</a
              >
            </li>
            <li class="nav-item" @click="activeTab = 'TODAY'">
              <a
                :class="{ 'nav-link': true, active: activeTab === 'TODAY' }"
                aria-current="page"
                href="#"
                >TODAY</a
              >
            </li>
            <li class="nav-item" @click="activeTab = 'NEXT'">
              <a
                :class="{ 'nav-link': true, active: activeTab === 'NEXT' }"
                aria-current="page"
                href="#"
                >NEXT</a
              >
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
                <tr v-for="meeting in groupedMeetings.PREV" :key="meeting.date">
                  <td>{{ meeting.date }}</td>
                  <td>{{ meeting.time }}</td>
                  <td
                    :class="{
                      agenda: true,
                      'bold-agenda':
                        selectedMeeting &&
                        selectedMeeting.date === meeting.date &&
                        selectedMeeting.agenda === meeting.agenda
                    }"
                    @click="selectMeeting(meeting)"
                  >
                    {{ meeting.agenda }}
                  </td>
                  <td>
                    <button
                      :class="buttonClass('PREV', meeting.status)"
                      @click="toggleStatus(meeting)"
                    >
                      {{ buttonText('PREV', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'TODAY'">
                <tr v-for="meeting in groupedMeetings.TODAY" :key="meeting.date">
                  <td>{{ meeting.date }}</td>
                  <td>{{ meeting.time }}</td>
                  <td
                    :class="{
                      agenda: true,
                      'bold-agenda':
                        selectedMeeting &&
                        selectedMeeting.date === meeting.date &&
                        selectedMeeting.agenda === meeting.agenda
                    }"
                    @click="selectMeeting(meeting)"
                  >
                    {{ meeting.agenda }}
                  </td>
                  <td>
                    <button
                      :class="buttonClass('TODAY', meeting.status)"
                      @click="toggleStatus(meeting)"
                    >
                      {{ buttonText('TODAY', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'NEXT'">
                <tr v-for="meeting in groupedMeetings.NEXT" :key="meeting.date">
                  <td>{{ meeting.date }}</td>
                  <td>{{ meeting.time }}</td>
                  <td
                    :class="{
                      agenda: true,
                      'bold-agenda':
                        selectedMeeting &&
                        selectedMeeting.date === meeting.date &&
                        selectedMeeting.agenda === meeting.agenda
                    }"
                    @click="selectMeeting(meeting)"
                  >
                    {{ meeting.agenda }}
                  </td>
                  <td>
                    <button
                      :class="buttonClass('NEXT', meeting.status)"
                      @click="toggleStatus(meeting)"
                    >
                      {{ buttonText('NEXT', meeting.status) }}
                    </button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </section>

        <section
          :class="{ 'meeting-detail-section': true, 'hidden-detail-section': !selectedMeeting }"
        >
          <template v-if="selectedMeeting">
            <div class="meeting-detail-header">
              <p>&nbsp;{{ selectedMeeting?.agenda }}&nbsp;</p>
              <button @click="closeMeetingDetails">X</button>
            </div>
            <div class="meeting-detail-content">
              <table class="meeting-detail-table">
                <tr>
                  <td><strong>Date</strong></td>
                  <td>{{ selectedMeeting?.date }}</td>
                </tr>
                <tr>
                  <td><strong>Time</strong></td>
                  <td>{{ selectedMeeting?.time }}</td>
                </tr>
                <tr>
                  <td><strong>Status</strong></td>
                  <td>
                    <button
                      :class="buttonClass(detailType, selectedMeeting?.status)"
                      @click="toggleDetailStatus(selectedMeeting)"
                    >
                      {{ buttonText(detailType, selectedMeeting?.status) }}
                    </button>
                  </td>
                </tr>
                <tr>
                  <td><strong>Members</strong></td>
                  <td class="show-member before-dropdown" @click="toggleMembersList">
                    {{ selectedMeeting?.members }} members joined!
                    <ul v-show="showMembersList" class="detail-dropdown dropdown">
                      <li
                        v-for="member in selectedMeetingMembers"
                        :key="member.name"
                        class="member"
                      >
                        <img :src="member.avatar" :alt="member.name" />{{ member.name }}
                      </li>
                    </ul>
                  </td>
                </tr>
                <tr>
                  <td><strong>Files</strong></td>
                  <td class="before-dropdown" @click="toggleFilesList">
                    {{ selectedMeeting?.files.length }} files uploaded
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
                        <a
                          href="#"
                          @click.prevent="
                            previewFile({ name: 'summary.pdf', link: selectedMeeting.summary })
                          "
                          class="file-link"
                          >📂summary</a
                        >
                      </td>
                      <td>
                        <a
                          href="#"
                          @click.prevent="
                            previewFile({ name: 'record.pdf', link: selectedMeeting.record })
                          "
                          class="file-link"
                          >📁record</a
                        >
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
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';

const inConference = ref(false);
const sessionId = ref(null);
const selectedMeeting = ref(null);
const detailType = ref('');
const showMembersList = ref(false);
const showTodayMembersList = ref(false);
const showFilesList = ref(false);
const showMemberListDropdown = ref(false);
const showOverlay = ref(false);
const todayMeetingMembers = ref([]);
const selectedMeetingMembers = ref([]);
const activeTab = ref('TODAY');
const departmentCreationDate = ref('2022-01-01');
const members = ref([
  { name: 'Robert', avatar: 'https://via.placeholder.com/32' },
  { name: 'Lisa', avatar: 'https://via.placeholder.com/32' },
  { name: 'Tom', avatar: 'https://via.placeholder.com/32' },
  { name: 'Mike', avatar: 'https://via.placeholder.com/32' },
  { name: 'Sophie', avatar: 'https://via.placeholder.com/32' },
  { name: 'Rachael', avatar: 'https://via.placeholder.com/32' }
]);
const meetings = ref([
{
          date: '2024-11-15',
          agenda: '현대자동차',
          status: 'IN',
          description: 'Detailed description of 현대자동차',
          time: '13PM-16PM',
          members: 7,
          files: [
            { name: '현대자동차.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-10-29',
          agenda: '현대오토에버',
          status: 'IN',
          description: 'Detailed description of 현대오토에버',
          time: '8AM-11AM',
          members: 3,
          files: [
            { name: '현대오토에버.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-10-05',
          agenda: '현대케피코',
          status: 'IN',
          description: 'Detailed description of 현대케피코',
          time: '16PM-18PM',
          members: 5,
          files: [{ name: '현대케피코.pdf', link: '#', uploader: 'Tom' }]
        },
        {
          date: '2024-09-15',
          agenda: '뱅킹 서비스',
          status: 'OUT',
          description: 'Detailed description of 뱅킹 서비스',
          time: '8AM-10AM',
          members: 8,
          files: [
            { name: 'bank_v4.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-08-26',
          agenda: '인스타그램',
          status: 'OUT',
          description: 'Detailed description of 인스타그램',
          time: '11AM-13PM',
          members: 5,
          files: [{ name: 'design.pdf', link: '#', uploader: 'Tom' }]
        },
        {
          date: '2024-07-26',
          agenda: '웹 RTC',
          status: 'IN',
          description: 'Detailed description of 웹 RTC',
          time: '15PM-17PM',
          members: 4,
          files: [{ name: 'rtc_spec.docx', link: '#', uploader: 'Mike' }]
        },
        {
          date: '2024-06-28',
          agenda: 'TTS',
          status: 'IN',
          description: 'Detailed description of TTS',
          time: '14PM-16PM',
          members: 6,
          files: [{ name: 'tts_plan.xlsx', link: '#', uploader: 'Sophie' }],
          summary: '/path/to/tts_summary.pdf',
          record: '/path/to/tts_record.pdf'
        },
        {
          date: '2024-07-23',
          agenda: 'AI 요약',
          status: 'OUT',
          description: 'Detailed description of AI 요약',
          time: '17PM-18PM',
          members: 4,
          files: [{ name: 'ai_summary.txt', link: '#', uploader: 'Rachael' }],
          summary: '/path/to/ai_summary.pdf',
          record: '/path/to/ai_record.pdf'
        },
        {
          date: '2024-06-13',
          agenda: 'STT',
          status: 'IN',
          description: 'Detailed description of STT',
          time: '20PM-22PM',
          members: 7,
          files: [{ name: 'stt_notes.doc', link: '#', uploader: 'Robert' }],
          summary: '/path/to/stt_summary.pdf',
          record: '/path/to/stt_record.pdf'
        },
        {
          date: '2024-05-14',
          agenda: '다국어 화상회의',
          status: 'IN',
          description: 'Detailed description of 다국어화상회의',
          time: '11AM-15PM',
          members: 4,
          files: [{ name: '다국어 화상회의_notes.doc', link: '#', uploader: 'Robert' }],
          summary: '/path/to/다국어 화상회의_summary.pdf',
          record: '/path/to/다국어 화상회의_record.pdf'
        }
]);

const isOwner = ref(false);
const previewUrl = ref(null);

const route = useRoute();
const router = useRouter();
const teamStore = useTeamStore();
const userStore = useUserStore();

const todayMeeting = computed(() => {
  const today = new Date().toISOString().split('T')[0];
  return meetings.value.find((meeting) => meeting.date === today);
});

const departmentName = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.teams.find((team) => team.id === teamId);
  return teamData ? teamData.teamName : '';
});

const groupedMeetings = computed(() => {
  const groups = {
    PREV: [],
    TODAY: [],
    NEXT: []
  };
  const today = new Date().toISOString().split('T')[0];
  const sortedMeetings = [...meetings.value].sort((a, b) => new Date(b.date) - new Date(a.date));
  sortedMeetings.forEach((meeting) => {
    if (meeting.date === today) {
      groups.TODAY.push(meeting);
    } else if (meeting.date > today) {
      groups.NEXT.push(meeting);
    } else {
      groups.PREV.push(meeting);
    }
  });
  return { NEXT: groups.NEXT, TODAY: groups.TODAY, PREV: groups.PREV };
});

const totalMeetingHours = computed(() => {
  return meetings.value.reduce((total, meeting) => {
    const [start, end] = meeting.time
      .split('-')
      .map((time) => parseInt(time.replace(/AM|PM/, '')));
    return total + (end - start);
  }, 0);
});

const prevMeetingHours = computed(() => {
  return groupedMeetings.value.PREV.reduce((total, meeting) => {
    const [start, end] = meeting.time
      .split('-')
      .map((time) => parseInt(time.replace(/AM|PM/, '')));
    return total + (end - start);
  }, 0);
});

const todayMeetingHours = computed(() => {
  return groupedMeetings.value.TODAY.reduce((total, meeting) => {
    const [start, end] = meeting.time
      .split('-')
      .map((time) => parseInt(time.replace(/AM|PM/, '')));
    return total + (end - start);
  }, 0);
});

const nextMeetingHours = computed(() => {
  return groupedMeetings.value.NEXT.reduce((total, meeting) => {
    const [start, end] = meeting.time
      .split('-')
      .map((time) => parseInt(time.replace(/AM|PM/, '')));
    return total + (end - start);
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

const startConference = async () => {
  try {
    const customSessionId = 'TestSession'; // 원하는 세션 ID로 변경 가능
    const response = await axios.post('http://localhost:5000/api/sessions', { customSessionId });

    sessionId.value = response.data.id;
    console.log('Starting conference with OpenVidu, sessionId:', sessionId.value);

    router
      .push({ name: 'ConferenceView', params: { sessionId: sessionId.value } })
      .then(() => {
        inConference.value = true;
      })
      .catch((err) => {
        console.error('Error navigating to ConferenceView:', err);
      });
  } catch (error) {
    console.error('Failed to create OpenVidu session:', error);
  }
};

const selectMeeting = (meeting) => {
  selectedMeeting.value = meeting;
  detailType.value = computeDetailType(meeting.date);
  selectedMeetingMembers.value = members.value.slice(0, meeting.members);
  showMembersList.value = false; // 초기에는 멤버 목록을 숨김
  showFilesList.value = false; // 초기에는 파일 목록을 숨김
  previewUrl.value = null; // 초기에는 파일 미리보기 URL 숨김
  showOverlay.value = true; // 오버레이 표시
};

const closeMeetingDetails = () => {
  selectedMeeting.value = null;
  selectedMeetingMembers.value = [];
  showMembersList.value = false;
  showFilesList.value = false;
  previewUrl.value = null; // 파일 미리보기 URL 초기화
  showOverlay.value = false; // 오버레이 숨김
};

const toggleStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const toggleDetailStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const buttonClass = (type, status) => {
  if (type === 'NEXT') {
    return status === 'IN' ? 'btn-green' : 'btn-red';
  } else if (type === 'PREV') {
    return 'btn-gray';
  } else if (type === 'TODAY') {
    return status === 'IN' ? 'btn-green' : 'btn-red';
  }
  return '';
};

const buttonText = (type, status) => {
  return status;
};

const computeDetailType = (date) => {
  const today = new Date().toISOString().split('T')[0];
  if (date === today) return 'TODAY';
  else if (date > today) return 'NEXT';
  else return 'PREV';
};

const toggleMemberListDropdown = () => {
  showMemberListDropdown.value = !showMemberListDropdown.value;
  showOverlay.value = showMemberListDropdown.value; // 오버레이 표시
};

const toggleTodayMembersList = () => {
  showTodayMembersList.value = !showTodayMembersList.value;
  if (todayMeeting.value) {
    todayMeetingMembers.value = members.value.slice(0, todayMeeting.value.members);
  }
  showOverlay.value = showTodayMembersList.value; // 오버레이 표시
};

const selectLatestTodayMeeting = () => {
  const todayMeetings = groupedMeetings.value.TODAY;
  if (todayMeetings.length > 0) {
    selectMeeting(todayMeetings[0]);
  }
};

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
  showTodayMembersList.value = false;
  showFilesList.value = false;
  showMembersList.value = false;
  showOverlay.value = false; // 오버레이 숨김
};

onMounted(() => {
  const teamId = parseInt(route.params.id, 10);
  const team = teamStore.teams.find(team => team.id === teamId);
  if (team) {
    isOwner.value = team.ownerId === userStore.userId;
  } else {
    console.error(`Team ${teamId} not found in store`);
  }
  selectLatestTodayMeeting();
});

watch(
  () => route.params.id,
  (newId) => {
    const teamId = parseInt(newId, 10);
    const team = teamStore.teams.find(team => team.id === teamId);
    if (team) {
      isOwner.value = team.ownerId === userStore.userId;
    } else {
      console.error(`Team ${teamId} not found in store`);
    }
  }
);

watch(activeTab, (newTab) => {
  if (newTab === 'TODAY') {
    selectLatestTodayMeeting();
  }
});
</script>

<!-- 

<script>
import { useTeamStore } from '@/stores/teamStore'
import { useUserStore } from '@/stores/userStore'
import axios from 'axios'

export default {
  name: 'ReadyPage',
  data() {
    return {
      inConference: false,
      sessionId: null, // OpenVidu 세션 ID
      selectedMeeting: null,
      detailType: '',
      showMembersList: false, // 멤버 목록 표시 여부
      showTodayMembersList: false, // 오늘 미팅 멤버 목록 표시 여부
      showFilesList: false, // 파일 목록 표시 여부
      showMemberListDropdown: false, // department 멤버 목록 표시 여부
      showOverlay: false,
      todayMeetingMembers: [], // 오늘 미팅 멤버 목록
      selectedMeetingMembers: [],
      activeTab: 'TODAY', // 초기 활성화 탭 설정
      departmentCreationDate: '2022-01-01', // 예시 생성일
      members: [
        { name: 'Robert', avatar: 'https://via.placeholder.com/32' },
        { name: 'Lisa', avatar: 'https://via.placeholder.com/32' },
        { name: 'Tom', avatar: 'https://via.placeholder.com/32' },
        { name: 'Mike', avatar: 'https://via.placeholder.com/32' },
        { name: 'Sophie', avatar: 'https://via.placeholder.com/32' },
        { name: 'Rachael', avatar: 'https://via.placeholder.com/32' }
      ],
      meetings: [
        {
          date: '2024-11-15',
          agenda: '현대자동차',
          status: 'IN',
          description: 'Detailed description of 현대자동차',
          time: '13PM-16PM',
          members: 7,
          files: [
            { name: '현대자동차.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-10-29',
          agenda: '현대오토에버',
          status: 'IN',
          description: 'Detailed description of 현대오토에버',
          time: '8AM-11AM',
          members: 3,
          files: [
            { name: '현대오토에버.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-10-05',
          agenda: '현대케피코',
          status: 'IN',
          description: 'Detailed description of 현대케피코',
          time: '16PM-18PM',
          members: 5,
          files: [{ name: '현대케피코.pdf', link: '#', uploader: 'Tom' }]
        },
        {
          date: '2024-09-15',
          agenda: '뱅킹 서비스',
          status: 'OUT',
          description: 'Detailed description of 뱅킹 서비스',
          time: '8AM-10AM',
          members: 8,
          files: [
            { name: 'bank_v4.pptx', link: '#', uploader: 'Lisa' },
            { name: 'services.png', link: '#', uploader: 'Robert' }
          ]
        },
        {
          date: '2024-08-26',
          agenda: '인스타그램',
          status: 'OUT',
          description: 'Detailed description of 인스타그램',
          time: '11AM-13PM',
          members: 5,
          files: [{ name: 'design.pdf', link: '#', uploader: 'Tom' }]
        },
        {
          date: '2024-07-25',
          agenda: '웹 RTC',
          status: 'IN',
          description: 'Detailed description of 웹 RTC',
          time: '15PM-17PM',
          members: 4,
          files: [{ name: 'rtc_spec.docx', link: '#', uploader: 'Mike' }]
        },
        {
          date: '2024-06-28',
          agenda: 'TTS',
          status: 'IN',
          description: 'Detailed description of TTS',
          time: '14PM-16PM',
          members: 6,
          files: [{ name: 'tts_plan.xlsx', link: '#', uploader: 'Sophie' }],
          summary: '/path/to/tts_summary.pdf',
          record: '/path/to/tts_record.pdf'
        },
        {
          date: '2024-07-23',
          agenda: 'AI 요약',
          status: 'OUT',
          description: 'Detailed description of AI 요약',
          time: '17PM-18PM',
          members: 4,
          files: [{ name: 'ai_summary.txt', link: '#', uploader: 'Rachael' }],
          summary: '/path/to/ai_summary.pdf',
          record: '/path/to/ai_record.pdf'
        },
        {
          date: '2024-06-13',
          agenda: 'STT',
          status: 'IN',
          description: 'Detailed description of STT',
          time: '20PM-22PM',
          members: 7,
          files: [{ name: 'stt_notes.doc', link: '#', uploader: 'Robert' }],
          summary: '/path/to/stt_summary.pdf',
          record: '/path/to/stt_record.pdf'
        },
        {
          date: '2024-05-14',
          agenda: '다국어 화상회의',
          status: 'IN',
          description: 'Detailed description of 다국어화상회의',
          time: '11AM-15PM',
          members: 4,
          files: [{ name: '다국어 화상회의_notes.doc', link: '#', uploader: 'Robert' }],
          summary: '/path/to/다국어 화상회의_summary.pdf',
          record: '/path/to/다국어 화상회의_record.pdf'
        }
      ],
      messages: [
        {
          id: 1,
          sender: 'Lisa',
          text: '첨부파일 참조 부탁드립니다.',
          avatar: 'https://via.placeholder.com/32'
        },
        {
          id: 2,
          sender: 'Lisa',
          text: '공유 감사합니다!',
          avatar: 'https://via.placeholder.com/32'
        }
      ],
      isOwner: false, // 소유자 여부를 저장할 변수
      previewUrl: null // 추후에 넣기
    }
  },
  computed: {
    todayMeeting() {
      const today = new Date().toISOString().split('T')[0]
      return this.meetings.find((meeting) => meeting.date === today)
    },
    departmentName() {
      const teamStore = useTeamStore()
      const teamId = parseInt(this.$route.params.id, 10) // 문자열을 숫자로 변환
      const teamData = teamStore.teams.find((team) => team.id === teamId)
      return teamData ? teamData.teamName : '' // teamName이 존재하면 반환
    },
    groupedMeetings() {
      const groups = {
        PREV: [],
        TODAY: [],
        NEXT: []
      }
      const today = new Date().toISOString().split('T')[0]
      const sortedMeetings = [...this.meetings].sort((a, b) => new Date(b.date) - new Date(a.date))
      sortedMeetings.forEach((meeting) => {
        if (meeting.date === today) {
          groups.TODAY.push(meeting)
        } else if (meeting.date > today) {
          groups.NEXT.push(meeting)
        } else {
          groups.PREV.push(meeting)
        }
      })
      return { NEXT: groups.NEXT, TODAY: groups.TODAY, PREV: groups.PREV }
    },
    totalMeetingHours() {
      return this.meetings.reduce((total, meeting) => {
        const [start, end] = meeting.time
          .split('-')
          .map((time) => parseInt(time.replace(/AM|PM/, '')))
        return total + (end - start)
      }, 0)
    },
    prevMeetingHours() {
      return this.groupedMeetings.PREV.reduce((total, meeting) => {
        const [start, end] = meeting.time
          .split('-')
          .map((time) => parseInt(time.replace(/AM|PM/, '')))
        return total + (end - start)
      }, 0)
    },
    todayMeetingHours() {
      return this.groupedMeetings.TODAY.reduce((total, meeting) => {
        const [start, end] = meeting.time
          .split('-')
          .map((time) => parseInt(time.replace(/AM|PM/, '')))
        return total + (end - start)
      }, 0)
    },
    nextMeetingHours() {
      return this.groupedMeetings.NEXT.reduce((total, meeting) => {
        const [start, end] = meeting.time
          .split('-')
          .map((time) => parseInt(time.replace(/AM|PM/, '')))
        return total + (end - start)
      }, 0)
    },
    prevMeetingHoursPercentage() {
      return (this.prevMeetingHours / this.totalMeetingHours) * 100
    },
    todayMeetingHoursPercentage() {
      return (this.todayMeetingHours / this.totalMeetingHours) * 100
    },
    nextMeetingHoursPercentage() {
      return (this.nextMeetingHours / this.totalMeetingHours) * 100
    }
  },
  methods: {
    async joinConference() {
      try {
        const response = await axios.get('http://localhost:5000/api/sessions');
        this.sessionId = response.data.sessionId;

        this.$router
          .push({ name: 'ConferenceView', params: { sessionId: this.sessionId } })
          .then(() => {
            this.inConference = true
          })
          .catch((err) => {
            console.error('Error navigating to ConferenceView:', err)
          })
      } catch (error) {
        console.error('Failed to join conference:', error);
      }
    },
    async startConference() {
      try {
        const customSessionId = 'TestSession'; // 원하는 세션 ID로 변경 가능
        const response = await axios.post('http://localhost:5000/api/sessions', { customSessionId });

        this.sessionId = response.data.id;
        console.log('Starting conference with OpenVidu, sessionId:', this.sessionId);

        this.$router
          .push({ name: 'ConferenceView', params: { sessionId: this.sessionId } })
          .then(() => {
            this.inConference = true
          })
          .catch((err) => {
            console.error('Error navigating to ConferenceView:', err)
          })
      } catch (error) {
        console.error('Failed to create OpenVidu session:', error);
      }
    },
    selectMeeting(meeting) {
      this.selectedMeeting = meeting;
      this.detailType = this.computeDetailType(meeting.date);
      this.selectedMeetingMembers = this.members.slice(0, meeting.members);
      this.showMembersList = false; // 초기에는 멤버 목록을 숨김
      this.showFilesList = false; // 초기에는 파일 목록을 숨김
      this.previewUrl = null; // 초기에는 파일 미리보기 URL 숨김
      this.showOverlay = true; // 오버레이 표시
    },
    closeMeetingDetails() {
      this.selectedMeeting = null;
      this.selectedMeetingMembers = [];
      this.showMembersList = false;
      this.showFilesList = false;
      this.previewUrl = null; // 파일 미리보기 URL 초기화
      this.showOverlay = false; // 오버레이 숨김
    },
    toggleStatus(meeting) {
      meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
    },
    toggleDetailStatus(meeting) {
      meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
    },
    buttonClass(type, status) {
      if (type === 'NEXT') {
        return status === 'IN' ? 'btn-green' : 'btn-red';
      } else if (type === 'PREV') {
        return 'btn-gray';
      } else if (type === 'TODAY') {
        return status === 'IN' ? 'btn-green' : 'btn-red';
      }
      return '';
    },
    buttonText(type, status) {
      return status;
    },
    computeDetailType(date) {
      const today = new Date().toISOString().split('T')[0];
      if (date === today) return 'TODAY';
      else if (date > today) return 'NEXT';
      else return 'PREV';
    },
    toggleMemberListDropdown() {
      this.showMemberListDropdown = !this.showMemberListDropdown;
      this.showOverlay = this.showMemberListDropdown; // 오버레이 표시
    },
    toggleTodayMembersList() {
      this.showTodayMembersList = !this.showTodayMembersList;
      if (this.todayMeeting) {
        this.todayMeetingMembers = this.members.slice(0, this.todayMeeting.members);
      }
      this.showOverlay = this.showTodayMembersList; // 오버레이 표시
    },
    selectLatestTodayMeeting() {
      const todayMeetings = this.groupedMeetings.TODAY;
      if (todayMeetings.length > 0) {
        this.selectMeeting(todayMeetings[0]);
      }
    },
    toggleFilesList() {
      this.showFilesList = !this.showFilesList;
      this.showOverlay = this.showFilesList;
    },
    toggleMembersList() {
      this.showMembersList = !this.showMembersList;
      this.showOverlay = this.showMembersList;
    },
    previewFile(file) {
      this.previewUrl = file.link;
    },
    closeDropdowns() {
      this.showMemberListDropdown = false;
      this.showTodayMembersList = false;
      this.showFilesList = false;
      this.showMembersList = false;
      this.showOverlay = false; // 오버레이 숨김
    }
  },
  async mounted() {
    const teamStore = useTeamStore();
    const userStore = useUserStore();
    const userId = userStore.userId;
    const teamId = parseInt(this.$route.params.id, 10);

    // 팀 데이터를 불러와 소유자 여부를 확인
    const team = teamStore.teams.find(team => team.id === teamId);
    if (team) {
      if (team.ownerId === userId) {
        this.isOwner = true;
      }
    } else {
      console.error(`Team ${teamId} not found in store`);
    }

    this.selectLatestTodayMeeting();

    // 사용자 정보를 업데이트하여 버튼 상태 동기화
    userStore.$subscribe((mutation, state) => {
      if (state.userId === team.ownerId) {
        this.isOwner = true;
      } else {
        this.isOwner = false;
      }
    });
  },
  watch: {
    $route(to, from) {
      if (to.name === 'rnd' && from.name === 'ConferenceView') {
        this.inConference = false;
      }
    },
    activeTab(newTab) {
      if (newTab === 'TODAY') {
<<<<<<< HEAD
        this.selectLatestTodayMeeting()
      }
    }
=======
        this.selectLatestTodayMeeting();
      }
    },
>>>>>>> develop_front
  }
};
</script>
 -->


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

.top-section {
  display: flex;
  justify-content: space-between;
  height: auto;
  flex-direction: column;
  padding: 1rem;
}

/* notice-and-intro */
.notice-and-intro {
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

/* department-info */
.department-table {
  width: 100%;
  border-collapse: collapse;
  /* margin-bottom: 1rem; */
}

.department-table td {
  padding: 0.3rem 0.5rem;
}

/* total-meeting-hours */
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
  /* margin-top: 10px; */
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

/* label */
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
  /* font-size: xx-small; */
  font-size: 9px;
  font-weight: bpld;
}


/* member */
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

/* main-section */
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

/* meeting-list */
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

.btn-play {
  background-color: rgba(225, 190, 231, 1);
  padding: 5px;
  border-radius: 50%;
  border: 2px dashed black;
}

.meeting-list tbody {
  display: block;
  max-height: 140px; /* 원하는 최대 높이 설정 */
  overflow-y: scroll;
}

.meeting-list tbody::-webkit-scrollbar {
  width: 0; /* 스크롤바의 너비를 0으로 설정 */
  background: transparent; /* 스크롤바 배경을 투명하게 설정 */
}

.meeting-list tr {
  display: table;
  width: calc(100% - 1rem); /* 테이블 너비를 100%에서 약간 줄임 */
  table-layout: fixed;
}


/* detail */
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

/* .files-section p {
  font-weight: bold;
  text-align: center;
  text-decoration-line: underline;  
  text-decoration-style: wavy;
  text-decoration-color: rgb(154, 130, 253);
  text-decoration-thickness: auto;
  margin-bottom: 5px;
} */

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

/* dropdown 공통 스타일 */
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

.dropdown-hidden {
  display: none;
}

/* 배경 클릭 시 dropdown 닫기 */
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