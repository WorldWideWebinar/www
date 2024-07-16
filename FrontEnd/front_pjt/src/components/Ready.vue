<template>
  <div v-if="!inConference" class="ready-page-container">
    <div class="top-and-chat">
      <div class="top-section">
        <header class="header">
          <h3>Welcome to <span class="highlight">R&D</span> Ready Page</h3>
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
                  <p>{{ todayMeeting.members }} members joined!</p>
                </div>
                <div class="notice-right">
                  <button @click="joinConference" class="join-button">
                    <span class="play-icon">▶</span>
                    <span class="join-text">GO!</span>
                  </button>
                </div>
              </div>
              <div v-else class="notice-item">
                <p>오늘 회의가 없습니다.</p>
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
        <table class="meeting-list">
          <thead>
            <tr>
              <th>DATE</th>
              <th>AGENDA</th>
              <th>JOIN</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="meeting in meetings" :key="meeting.date" @click="selectMeeting(meeting)">
              <td>{{ meeting.date }}</td>
              <td>{{ meeting.agenda }}</td>
              <td>
                <button :class="meeting.status">{{ meeting.status }}</button>
              </td>
            </tr>
          </tbody>
        </table>
      </section>
      <section v-if="selectedMeeting" class="meeting-detail-section">
        <div class="meeting-detail-header">
          <h5 style="font-weight: bolder; color:blueviolet">{{ selectedMeeting.agenda }}</h5>
          <button @click="closeMeetingDetails">X</button>
        </div>
        <div class="meeting-detail-content">
          <p><strong>Date:</strong> {{ selectedMeeting.date }}</p>
          <p><strong>Time:</strong> {{ selectedMeeting.time }}</p>
          <p><strong>Status:</strong> <button class="status-btn">{{ selectedMeeting.status }}</button></p>
          <p><strong>{{ selectedMeeting.members }} members joined!</strong></p>
          <div class="files-section">
            <div>
              <a href="#" class="file-link">AI_summary</a>
              <a href="#" class="file-link">AI_record</a>
            </div>
            <ul>
              <li v-for="file in selectedMeeting.files" :key="file.name">
                <a :href="file.link" download>{{ file.name }}</a> uploaded by {{ file.uploader }}
              </li>
            </ul>
            <a href="#" class="more-files-link">> more files</a>
          </div>
        </div>
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
      members: [
        { name: 'Robert', avatar: 'https://via.placeholder.com/32' },
        { name: 'Lisa', avatar: 'https://via.placeholder.com/32' },
        { name: 'Tom', avatar: 'https://via.placeholder.com/32' },
        { name: 'Mike', avatar: 'https://via.placeholder.com/32' },
        { name: 'Sophie', avatar: 'https://via.placeholder.com/32' },
        { name: 'Rachael', avatar: 'https://via.placeholder.com/32' }
      ],
      meetings: [
        { date: '2024-09-15', agenda: '뱅킹 서비스', status: 'IN', description: 'Detailed description of 뱅킹 서비스', time: '14PM-16PM', members: 8, files: [{ name: 'bank_v4.pptx', link: '#', uploader: 'Lisa' }, { name: 'services.png', link: '#', uploader: 'Robert' }] },
        { date: '2024-08-26', agenda: '인스타그램', status: 'OUT', description: 'Detailed description of 인스타그램', time: '14PM-16PM', members: 5, files: [{ name: 'design.pdf', link: '#', uploader: 'Tom' }] },
        { date: '2024-07-16', agenda: '웹 RTC', status: 'IN', description: 'Detailed description of 웹 RTC', time: '14PM-16PM', members: 8, files: [{ name: 'rtc_spec.docx', link: '#', uploader: 'Mike' }] },
        { date: '2024-06-28', agenda: 'TTS', status: 'IN', description: 'Detailed description of TTS', time: '14PM-16PM', members: 6, files: [{ name: 'tts_plan.xlsx', link: '#', uploader: 'Sophie' }] },
        { date: '2024-06-22', agenda: 'AI 요약', status: 'OUT', description: 'Detailed description of AI 요약', time: '14PM-16PM', members: 4, files: [{ name: 'ai_summary.txt', link: '#', uploader: 'Rachael' }] },
        { date: '2024-06-13', agenda: 'STT', status: 'IN', description: 'Detailed description of STT', time: '14PM-16PM', members: 7, files: [{ name: 'stt_notes.doc', link: '#', uploader: 'Robert' }] }
      ],
      messages: [
        { id: 1, sender: 'Lisa', text: '첨부파일 참조 부탁드립니다.', avatar: 'https://via.placeholder.com/32' },
        { id: 2, sender: 'Lisa', text: '공유 감사합니다!', avatar: 'https://via.placeholder.com/32' }
      ]
    }
  },
  computed: {
    todayMeeting() {
      const today = new Date().toISOString().split('T')[0];
      return this.meetings.find(meeting => meeting.date === today);
    }
  },
  methods: {
    joinConference() {
      this.inConference = true;
      this.$router.push({ name: 'ConferenceView' });
    },
    selectMeeting(meeting) {
      this.selectedMeeting = meeting;
    },
    closeMeetingDetails() {
      this.selectedMeeting = null;
    }
  },
  watch: {
    '$route'(to, from) {
      if (to.name === 'rnd' && from.name === 'ConferenceView') {
        this.inConference = false;
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
  padding-bottom: 1rem;
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

.notice-header, .member-header, .meeting-header {
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

.notice-left, .notice-middle, .notice-right {
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

.notice-left::after, .notice-middle::after {
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
  background-color: #f8bbd0;
  border: none;
  border-radius: 50%;
  padding: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
}

.notice-right .play-icon {
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
  padding: 1rem;
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
  margin-bottom: 1rem;
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
  flex: 4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: calc(100% - 2rem); /* notice-section과 동일한 가로 길이 */
}

/* detail */
.meeting-detail-section {
  flex: 1.4;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-left: 1rem;
  width: calc(100% - (1.3 * 4rem + 1rem)); /* member-section과 chat-section을 합친 가로 길이 */
}

.meeting-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.meeting-detail-content {
  display: flex;
  flex-direction: column;
}

.status-btn {
  background-color: #6c757d;
  border: none;
  border-radius: 8px;
  color: white;
  padding: 0.5rem 1rem;
}

.files-section {
  margin-top: 1rem;
  border-top: 1px dashed #ccc;
  padding-top: 1rem;
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

.meeting-header, .meeting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.meeting-list {
  width: 100%;
  border-collapse: collapse;
}

.meeting-list th, .meeting-list td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: center;
}

.notice-item button, .add-member-btn, .add-meeting-btn, .chat-input button {
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
  .member-section, .chat-section {
    display: none;
  }

  .notice-and-member {
    flex-direction: column;
  }

  .notice-section {
    flex: 1;
  }
}
</style>
