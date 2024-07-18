<template>
  <div v-if="!inConference" class="ready-page-container">
    <div class="top-and-chat">
      <div class="top-section">
        <header class="header">
          <h3>
            Welcome to <span class="highlight">{{ departmentName }}</span> Ready Page
          </h3>
        </header>
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
                  <button @click="joinConference" class="join-button">
                    <span>▶ GO!</span>
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
              <h5 style="font-weight: bolder">👤 Member</h5>
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
      <aside class="chat-section">
        <h5 style="font-weight: bolder">🔈 Chat</h5>
        <div class="chat-content">
          <div class="chat-message" v-for="message in messages" :key="message.id">
            <img :src="message.avatar" :alt="message.sender" />
            <div>
              <p>{{ message.sender }}</p>
              <p>{{ message.text }}</p>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <input type="text" placeholder="메시지를 입력하세요" />
          <button>보내기</button>
        </div>
      </aside>
    </div>
    <main class="main-section">
      <section class="meeting-list-section">
        <div class="meeting-header">
          <h5 style="font-weight: bolder">🖥️ Meeting List</h5>
          <button class="add-meeting-btn">+</button>
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
                <td class="agenda" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
                <td>
                  <button :class="buttonClass('PREV', meeting.status)" @click="toggleStatus(meeting)">{{ buttonText('PREV', meeting.status) }}</button>
                </td>
              </tr>
            </template>
            <template v-if="activeTab === 'TODAY'">
              <tr v-for="meeting in groupedMeetings.TODAY" :key="meeting.date">
                <td>{{ meeting.date }}</td>
                <td>{{ meeting.time }}</td>
                <td class="agenda" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
                <td>
                  <button :class="buttonClass('TODAY', meeting.status)" @click="toggleStatus(meeting)">{{ buttonText('TODAY', meeting.status) }}</button>
                </td>
              </tr>
            </template>
            <template v-if="activeTab === 'NEXT'">
              <tr v-for="meeting in groupedMeetings.NEXT" :key="meeting.date">
                <td>{{ meeting.date }}</td>
                <td>{{ meeting.time }}</td>
                <td class="agenda" @click="selectMeeting(meeting)">{{ meeting.agenda }}</td>
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
            <h5 style="font-weight: bolder; color: blueviolet">{{ selectedMeeting?.agenda }}</h5>
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
            </table>
            <table class="files-section">
              <thead>
                <tr>
                  <th colspan="2">
                    <div>
                      <a href="#" class="file-link">AI_summary</a>
                      <a href="#" class="file-link">AI_record</a>
                    </div>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="file in selectedMeeting?.files" :key="file.name">
                  <td><a :href="file.link" download>{{ file.name }}</a></td>
                  <td>uploaded by {{ file.uploader }}</td>
                </tr>
              </tbody>
            </table>
            <a href="#" class="more-files-link">> more files</a>
          </div>
        </template>
      </section>
      
    </main>
  </div>
  <router-view v-else></router-view>
</template>

<script>
export default {
  name: 'ReadyPage',
  data() {
    return {
      inConference: false,
      selectedMeeting: null,
      detailType: '',
      showMembersList: false, // 멤버 목록 표시 여부
      showTodayMembersList: false, // 오늘 미팅 멤버 목록 표시 여부
      todayMeetingMembers: [], // 오늘 미팅 멤버 목록
      selectedMeetingMembers: [],
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
          date: '2024-07-18',
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
          date: '2024-06-22',
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
      ]
    }
  },
  computed: {
    todayMeeting() {
      const today = new Date().toISOString().split('T')[0]
      return this.meetings.find((meeting) => meeting.date === today)
    },
    departmentName() {
      return this.$route.params.name
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
  },
  methods: {
    joinConference() {
      this.inConference = true;
      this.$router.push({ name: 'ConferenceView' });
    },
    selectMeeting(meeting) {
      this.selectedMeeting = meeting;
      this.detailType = this.computeDetailType(meeting.date);
      this.selectedMeetingMembers = this.members.slice(0, meeting.members);
      this.showMembersList = false; // 초기에는 멤버 목록을 숨김
    },
    closeMeetingDetails() {
      this.selectedMeeting = null;
      this.selectedMeetingMembers = [];
      this.showMembersList = false;
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
        return status === 'IN' ? 'btn-play' : 'btn-red';
      }
      return '';
    },
    buttonText(type, status) {
      if (type === 'TODAY' && status === 'IN') {
        return '▶ GO!';
      }
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
  },
  watch: {
    $route(to, from) {
      if (to.name === 'rnd' && from.name === 'ConferenceView') {
        this.inConference = false
      }
    }
  }
}
</script>



<style scoped>
.ready-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 1rem;
}

.top-and-chat {
  display: flex;
  justify-content: space-between;
  height: auto;
}

.top-section {
  flex: 3;
  display: flex;
  flex-direction: column;
}

.header {
  text-align: center;
  padding: 0.5rem 0 0.5rem 0;
  /* background-color: #ffffff; */
}

.highlight {
  color: blueviolet;
}

/* notice-and-member */
.notice-and-member {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  gap: 1rem;
  width: 100%;
}

.notice-section {
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.member-section {
  flex: 1.3;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  /* font-weight: bold; */
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
  /* flex-direction: column; GO! 가로 배치*/
  align-items: center;
  justify-content: center;
  background-color: #e1bee7;
  border: none;
  border-radius: 50%;
  padding: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
  border-radius: 8px;
}

.notice-right {
  font-size: 1.2rem;
  padding: 5px;
  /* margin-right: 0.5rem; */
  /* margin-bottom: 0.5rem; */
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

/* chat */
.chat-section {
  flex: 1;
  margin-left: 1rem;
  background-color: #ffffff;
  padding: 1rem 1rem 0 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1rem;
}

.chat-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 0;
}

.chat-message img {
  border-radius: 50%;
  margin-right: 0.5rem;
}

.chat-input {
  display: flex;
  align-items: center;
}

.chat-input input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-right: 0.5rem;
}

/* main-section */
.main-section {
  display: flex;
  padding: 1rem 0 1rem 1rem;
  justify-content: space-between;
  width: 100%;
}

.meeting-list-section {
  flex: 3;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: 2rem;
}

.meeting-detail-section {
  flex: 1.07;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: calc(100% - (1.3 * 4rem + 1rem)); /* member-section과 chat-section을 합친 가로 길이 */
}

/* meeting-list */
.meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.meeting-list {
  width: 100%;
  border-collapse: collapse;
}

.meeting-list th,
.meeting-list td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: center;
}

.meeting-list th:nth-child(1),
.meeting-list td:nth-child(1) {
  width: 15%; /* TYPE 열 너비 */
}

.meeting-list th:nth-child(2),
.meeting-list td:nth-child(2) {
  width: 15%; /* DATE 열 너비 */
}

.meeting-list th:nth-child(3),
.meeting-list td:nth-child(3) {
  width: auto; /* AGENDA 열 너비 */
}


.meeting-list th:nth-child(4),
.meeting-list td:nth-child(4) {
  width: 15%; /* JOIN 열 너비 */
}

.meeting-list td:nth-child(3):hover,
.meeting-list td:nth-child(4):hover,
.meeting-list td:nth-child(3):hover *,
.meeting-list td:nth-child(4):hover *,
.meeting-list td.agenda:hover {
  font-weight: bolder;
  cursor: pointer;
}



/* additional styling */
.meeting-list th {
  background-color: #f5f5f5;
  font-weight: bold;
}

.meeting-list td button {
  border: none;
  border-radius: 8px;
  width: 80px;
  padding: 5px 10px;
  cursor: pointer;
  text-align: center;
}

button {
  border: none;
  border-radius: 8px;
  width: 80px;
  padding: 5px 10px;
  cursor: pointer;
  text-align: center;
}

.btn-green {
  background-color: rgba(139, 195, 74, 0.5); /* 초록색 배경을 흐리게 */
  color: black;
}

.btn-red {
  background-color: rgba(244, 67, 54, 0.5); /* 빨간색 배경을 흐리게 */
  color: black;
}

.btn-gray {
  background-color: rgba(108, 117, 125, 0.5); /* 회색 배경을 흐리게 */
  color: black;
}

.btn-play {
  /* background-color: rgba(225, 190, 231, 0.5); 보라색 배경을 흐리게 */
  background-color: rgba(225, 190, 231, 1);
  padding: 5px;
  border-radius: 50%;
  border: 2px dashed black; /* dashed border 추가 */
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
}

.meeting-detail-table td {
  padding: 0.5rem;
  /* border: 1px solid #ddd; */
}

.files-section {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  border-top: 2px dashed #ccc;
  padding-top: 1rem;
}

.files-section th,
.files-section td {
  padding: 0.5rem;
  /* border: 1px solid #ddd; */
}

.file-link {
  display: inline-block;
  margin-right: 1rem;
  color: #28a745;
}

.more-files-link {
  display: inline-block;
  margin-top: 1rem;
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}


/* dropdown 공통 스타일 */
.before-dropdown {
  text-decoration: underline;
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

/* notice-dropdown 특정 스타일 */
.notice-dropdown {
  width: 150px;
  left: 50%;
  transform: translateX(-50%);
}

/* details-dropdown 특정 스타일 */
.details-dropdown {
  width: 150px;
}




.add-member-btn,
.add-meeting-btn,
.chat-input button {
  background-color: #f8bbd0;
  border: none;
  border-radius: 8px;
  padding: 0.5rem;
  cursor: pointer;
  text-align: center;
}

.bold {
  font-weight: bold;
  margin: 0;
}


/* 화면 축소 시 member-section과 chat-section 숨기기 */
@media (max-width: 768px) {
  .member-section,
  .chat-section {
    display: none;
  }

  .notice-and-member {
    flex-direction: column;
  }

  .notice-section {
    flex: 1;
  }
}

/* 화면 축소 시 meeting-detail-section 숨기기 */
@media (max-width: 768px) {
  .meeting-detail-section {
    display: none;
  }
}
</style>
