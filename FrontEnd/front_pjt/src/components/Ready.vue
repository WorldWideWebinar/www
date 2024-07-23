<template>
  <div v-if="!inConference" class="ready-page-container">
    <header class="header">
      <h3>Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page</h3>
    </header>
    <div class="sub-container">
      <div class="top-section">
        <div class="notice-and-member">
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
                  <p class="before-dropdown" @click="toggleTodayMembersList">{{ todayMeeting.members }} members joined!</p>
                  <ul v-show="showTodayMembersList" class="notice-dropdown dropdown">
                    <li v-for="member in todayMeetingMembers" :key="member.name">{{ member.name }}</li>
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
          <section class="member-section">
            <div class="member-header">
              <h5 style="font-weight: bolder">👤</h5>
              <span>{{ members.length }}</span>
            </div>
            <div class="members">
              <div v-for="member in members" :key="member.name" class="member">
                <img :src="member.avatar" :alt="member.name" />
                <p>{{ member.name }}</p>
              </div>
            </div>
            <button class="add-member-btn">+</button>
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
              <a :class="{'nav-link': true, active: activeTab === 'PREV'}" aria-current="page" href="#">PREV</a>
            </li>
            <li class="nav-item" @click="activeTab = 'TODAY'">
              <a :class="{'nav-link': true, active: activeTab === 'TODAY'}" aria-current="page" href="#">TODAY</a>
            </li>
            <li class="nav-item" @click="activeTab = 'NEXT'">
              <a :class="{'nav-link': true, active: activeTab === 'NEXT'}" aria-current="page" href="#">NEXT</a>
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
                  <td :class="{'agenda': true, 'bold-agenda': selectedMeeting && selectedMeeting.date === meeting.date && selectedMeeting.agenda === meeting.agenda}" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
                  <td>
                    <button :class="buttonClass('PREV', meeting.status)" @click="toggleStatus(meeting)">{{ buttonText('PREV', meeting.status) }}</button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'TODAY'">
                <tr v-for="meeting in groupedMeetings.TODAY" :key="meeting.date">
                  <td>{{ meeting.date }}</td>
                  <td>{{ meeting.time }}</td>
                  <td :class="{'agenda': true, 'bold-agenda': selectedMeeting && selectedMeeting.date === meeting.date && selectedMeeting.agenda === meeting.agenda}" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
                  <td>
                    <button :class="buttonClass('TODAY', meeting.status)" @click="toggleStatus(meeting)">{{ buttonText('TODAY', meeting.status) }}</button>
                  </td>
                </tr>
              </template>
              <template v-if="activeTab === 'NEXT'">
                <tr v-for="meeting in groupedMeetings.NEXT" :key="meeting.date">
                  <td>{{ meeting.date }}</td>
                  <td>{{ meeting.time }}</td>
                  <td :class="{'agenda': true, 'bold-agenda': selectedMeeting && selectedMeeting.date === meeting.date && selectedMeeting.agenda === meeting.agenda}" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
                  <td>
                    <button :class="buttonClass('NEXT', meeting.status)" @click="toggleStatus(meeting)">{{ buttonText('NEXT', meeting.status) }}</button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </section>

        <section :class="{'meeting-detail-section': true, 'hidden-detail-section': !selectedMeeting}">
          <template v-if="selectedMeeting">
            <div class="meeting-detail-header">
              <h5 style="font-weight: bolder; color: blueviolet; margin: 0 auto;">{{ selectedMeeting?.agenda }}</h5>
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
                    <button :class="buttonClass(detailType, selectedMeeting?.status)" @click="toggleDetailStatus(selectedMeeting)">
                      {{ buttonText(detailType, selectedMeeting?.status) }}
                    </button>
                  </td>
                </tr>
                <tr>
                  <td><strong>Members</strong></td>
                  <td class="show-member before-dropdown" @click="showMemberList">
                    {{ selectedMeeting?.members }} members joined!
                    <ul v-show="showMembersList" class="detail-dropdown dropdown">
                      <li v-for="member in selectedMeetingMembers" :key="member.name">{{ member.name }}</li>
                    </ul>
                  </td>
                </tr>
                <tr>
                  <td><strong>Files</strong></td>
                  <td class="before-dropdown" @click="toggleFilesList">
                    {{ selectedMeeting?.files.length }} files uploaded
                    <ul v-show="showFilesList" class="detail-dropdown dropdown">
                      <li v-for="file in selectedMeeting?.files" :key="file.name">
                        <a :href="file.link" download>{{ file.name }}</a> uploaded by {{ file.uploader }}
                      </li>
                    </ul>
                  </td>
                </tr>
              </table>
              <div class="dash-separator"></div>
              <table class="files-section">
                <thead>
                  <tr>
                    <td><a href="#" class="file-link">AI_summary</a></td>
                    <td><a href="#" class="file-link">AI_record</a></td>
                  </tr>
                </thead>
              </table>
            </div>
          </template>
        </section>
      </main>
    </div>
  </div>
  <router-view v-else></router-view>
</template>

<script>
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';

export default {
  name: 'ReadyPage',
  data() {
    return {
      inConference: false,
      sessionId: null, // OpenVidu 세션 ID
      selectedMeeting: null,
      detailType: '',
      showMembersList: false,
      showTodayMembersList: false,
      showFilesList: false,
      todayMeetingMembers: [],
      selectedMeetingMembers: [],
      activeTab: 'TODAY',
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
          date: '2024-09-15',
          agenda: '뱅킹 서비스',
          status: 'IN',
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
          date: '2024-07-19',
          agenda: '웹 RTC',
          status: 'IN',
          description: 'Detailed description of 웹 RTC',
          time: '15PM-17PM',
          members: 8,
          files: [{ name: 'rtc_spec.docx', link: '#', uploader: 'Mike' }]
        },
        {
          date: '2024-06-28',
          agenda: 'TTS',
          status: 'IN',
          description: 'Detailed description of TTS',
          time: '14PM-16PM',
          members: 6,
          files: [{ name: 'tts_plan.xlsx', link: '#', uploader: 'Sophie' }]
        },
        {
          date: '2024-07-23',
          agenda: 'AI 요약',
          status: 'OUT',
          description: 'Detailed description of AI 요약',
          time: '17PM-18PM',
          members: 4,
          files: [{ name: 'ai_summary.txt', link: '#', uploader: 'Rachael' }]
        },
        {
          date: '2024-06-13',
          agenda: 'STT',
          status: 'IN',
          description: 'Detailed description of STT',
          time: '20PM-22PM',
          members: 7,
          files: [{ name: 'stt_notes.doc', link: '#', uploader: 'Robert' }]
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
      isOwner: false // 소유자 여부를 저장할 변수
    }
  },
  computed: {
    todayMeeting() {
      const today = new Date().toISOString().split('T')[0];
      return this.meetings.find((meeting) => meeting.date === today);
    },
    departmentName() {
      const teamStore = useTeamStore();
      const teamId = parseInt(this.$route.params.id, 10); // 문자열을 숫자로 변환
      const teamData = teamStore.teams.find(team => team.id === teamId);
      return teamData ? teamData.teamName : ''; // teamName이 존재하면 반환
    },
    groupedMeetings() {
      const groups = {
        PREV: [],
        TODAY: [],
        NEXT: []
      };
      const today = new Date().toISOString().split('T')[0];
      const sortedMeetings = [...this.meetings].sort((a, b) => new Date(b.date) - new Date(a.date));
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
    },
  },
  methods: {
    joinConference() {
      this.$router.push({ name: 'ConferenceView', params: { sessionId: this.sessionId } }).then(() => {
        this.inConference = true;
      }).catch(err => {
        console.error('Error navigating to ConferenceView:', err);
      });
    },
    async startConference() {
      try {
        // 커스텀 세션 ID 설정
        const customSessionId = 'TestSession'; // 원하는 세션 ID로 변경 가능
        const response = await axios.post('http://localhost:5000/api/sessions', { customSessionId });

        this.sessionId = response.data.id;
        console.log('Starting conference with OpenVidu, sessionId:', this.sessionId);

        this.$router.push({ name: 'ConferenceView', params: { sessionId: this.sessionId } }).then(() => {
          this.inConference = true;
        }).catch(err => {
          console.error('Error navigating to ConferenceView:', err);
        });
      } catch (error) {
        console.error('Failed to create OpenVidu session:', error);
      }
    },
    selectMeeting(meeting) {
      this.selectedMeeting = meeting;
      this.detailType = this.computeDetailType(meeting.date);
      this.selectedMeetingMembers = this.members.slice(0, meeting.members);
      this.showMembersList = false;
      this.showFilesList = false;
    },
    closeMeetingDetails() {
      this.selectedMeeting = null;
      this.selectedMeetingMembers = [];
      this.showMembersList = false;
      this.showFilesList = false;
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
    showMemberList() {
      this.showMembersList = !this.showMembersList;
    },
    toggleTodayMembersList() {
      this.showTodayMembersList = !this.showTodayMembersList;
      if (this.todayMeeting) {
        this.todayMeetingMembers = this.members.slice(0, this.todayMeeting.members);
      }
    },
    selectLatestTodayMeeting() {
      const todayMeetings = this.groupedMeetings.TODAY;
      if (todayMeetings.length > 0) {
        this.selectMeeting(todayMeetings[0]);
      }
    },
    toggleFilesList() {
      this.showFilesList = !this.showFilesList;
    }
  },
  mounted() {
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
  },
  watch: {
    $route(to, from) {
      if (to.name === 'rnd' && from.name === 'ConferenceView') {
        this.inConference = false;
      }
    },
    activeTab(newTab) {
      if (newTab === 'TODAY') {
        this.selectLatestTodayMeeting();
      }
    }
  }
};
</script>

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
  padding: 1rem;
}

.highlight {
  color: blueviolet;
}

.sub-container {
  width: 80%;
  margin: 0 auto; 
}

.top-section {
  display: flex;
  justify-content: space-between;
  height: auto;
  flex-direction: column;
  padding: 1rem;
}

/* notice-and-member */
.notice-and-member {
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

.member-section {
  flex: 1.5;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.notice-header,
.member-header,
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

.add-member-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8bbd0;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  margin: 0 auto;
}

/* main-section */
.main-section {
  display: flex;
  padding: 1rem;
  justify-content: space-between;
  width: 100%;
  gap: 2rem;
  box-sizing: border-box;
  height: 420px;
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
  padding: 1rem;
  border-radius: 8px;
  box-sizing: border-box;
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
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0 0 0;
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
  font-weight: bolder;
  cursor: pointer;
}

.meeting-list td:nth-child(3):hover,
.meeting-list td:nth-child(3):hover *,
.meeting-list td.agenda:hover {
  font-weight: bolder;
  color: blueviolet;
  cursor: pointer;
}

.bold-agenda {
  font-weight: bolder !important;
  color: blueviolet;
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

/* detail */
.meeting-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.meeting-detail-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
  font-size: medium;
}

.meeting-detail-table td {
  padding: 0.3rem 0.5rem;
  font-size: medium
}

.files-section {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  /* border-top: 2px dashed #ccc; */
  padding-top: 1rem;
}

.files-section th,
.files-section td {
  padding: 0.5rem;
}

.file-link {
  display: inline-block;
  color: blueviolet;
  font-weight: bold;
  text-decoration: underline;
}

.more-files-link {
  display: inline-block;
  margin-top: 1rem;
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}

.dash-separator {
  border-top: 2px dashed #ccc;
  margin: 1rem 0;
}

/* dropdown 공통 스타일 */
.before-dropdown {
  text-decoration: underline;
  font-size: medium;
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

.add-member-btn,
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

@media (max-width: 768px) {
  .member-section,
  .chat-section,
  .meeting-detail-section {
    display: none;
  }

  .sub-container {
    width: 100%;
  }
}
</style>
