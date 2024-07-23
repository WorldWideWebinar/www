<template>
  <div class="conference-container">
    <header class="header">
      <h3>Welcome to <span class="highlight">{{ departmentName }}</span> Meeting</h3>
    </header>
    <main class="main-content">
      <div class="left-side">
        <div class="participant" v-for="participant in participants" :key="participant.name">
          <div class="participant-video">
            <img :src="participant.avatar" alt="participant avatar" />
          </div>
          <div class="participant-info">
            <span>{{ participant.name }}</span>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="upper-section">
          <div class="presentation">
            <img src="https://via.placeholder.com/450x350" alt="Presentation Screenshot" />
          </div>
          <div class="right-side">
            <div class="participant">
              <div class="participant-video">
                <!-- <img src="https://via.placeholder.com/150x110" alt="Your avatar" /> -->
                <div id="video-container"></div>
              </div>
              <div class="participant-info">
                <span>나</span>
              </div>
            </div>
          </div>
        </div>
        <div class="translation-container">
          <div class="translation-section original">
            <h5>Original Version</h5>
            <div class="translation-content">
              <div class="message-group">
                <div class="speaker-info">
                  <strong>Robert</strong>
                  <div class="language"><span>🌐 영어</span></div>
                </div>
                <div class="message">
                  <span>Please brief me on this month’s inventory status.</span>
                </div>
              </div>
              <div class="message-group">
                <div class="speaker-info">
                  <strong>Lisa</strong>
                  <div class="language"><span>🌐 중국어</span></div>
                </div>
                <div class="message">
                  <span>包括预计明天到益山港的400吨在内，共有5600吨。这个季度的生产没有问题。</span>
                </div>
              </div>
            </div>
          </div>
          <div class="translation-section">
            <h5>Translated Version <span class="language-icon">🌐 한국어</span></h5>
            <div class="translation-content">
              <div class="message-group">
                <div class="speaker-info">
                  <strong>로버트</strong>
                </div>
                <div class="message">
                  <span>이번달 재고 현황에 대해 브리핑 부탁해.</span>
                </div>
              </div>
              <div class="message-group">
                <div class="speaker-info">
                  <strong>리사</strong>
                </div>
                <div class="message">
                  <span>내일 부산항에 도착 예정인 400톤을 포함하면 총 5600톤이야. 이번 분기 생산에는 문제 없을 것으로 예상돼.</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="footer">
          <div class="footer-left">
            <span style="font-weight: bold;">Duration</span>
            <span>1:27:31 30min left</span>
          </div>
          <div class="footer-center">
            <span style="font-weight: bold;">Attendance</span>
            <span>4 / 6</span>
          </div>
          <div class="footer-right">
            <span>Invite Alex, Joy</span>
          </div>
        </div>
      </div>
    </main>
    <div class="bottom-toolbar">
      <button class="btn-icon">🎤</button>
      <button class="btn-icon">🎥</button>
      <button class="btn-icon">🔇</button>
      <button class="btn-icon">🔄</button>
    </div>
  </div>
</template>

<script>
import { OpenVidu } from 'openvidu-browser';
import { useSessionStore } from '@/stores/sessionStore';
import axios from 'axios';

export default {
  name: 'ConferenceView',
  props: ['sessionId'],
  data() {
    return {
      participants: [
        { name: 'Robert', avatar: 'https://via.placeholder.com/150x110' },
        { name: 'Lisa', avatar: 'https://via.placeholder.com/150x110' },
        { name: 'Kevin', avatar: 'https://via.placeholder.com/150x110' }
      ],
      session: null,
      audioContext: null,
      processor: null,
      input: null,
      stream: null,
      userId: 'user_' + Math.floor(Math.random() * 10000)
    };
  },
  computed: {
    departmentName() {
      return this.$route.params.name;
    }
  },
  methods: {
    async joinSession() {
      const sessionStore = useSessionStore();
      const OV = new OpenVidu();
      const session = OV.initSession();
      sessionStore.setSession(session);

      session.on('streamCreated', (event) => {
        const subscriber = session.subscribe(event.stream, 'video-container');
        sessionStore.addStream(subscriber.stream);
      });

      try {
        // 백엔드 서버로 요청을 보내어 OpenVidu 세션에 연결하기 위한 토큰 생성
        const tokenResponse = await axios.post(`http://localhost:5000/api/sessions/${this.sessionId}/connection`);
        const token = tokenResponse.data.token;

        await session.connect(token, { clientData: 'Participant' });

        const publisher = OV.initPublisher('video-container', {
          videoSource: undefined,
          audioSource: undefined,
          publishVideo: true,
          publishAudio: true,
          resolution: '240x135',
          frameRate: 30,
          insertMode: 'APPEND'
        });
        session.publish(publisher);

        this.startCapturing();
      } catch (error) {
        console.error('Error connecting to session:', error);
      }
    },
    leaveSession() {
      if (this.session) {
        this.session.disconnect();
        this.session = null;
      }
      this.stopCapturing();
    },
    async startCapturing() {
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
      this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.input = this.audioContext.createMediaStreamSource(this.stream);
      this.processor = this.audioContext.createScriptProcessor(4096, 1, 1);

      this.processor.onaudioprocess = (e) => {
        const inputData = e.inputBuffer.getChannelData(0);
        const float32Array = new Float32Array(inputData);
        console.log(`Captured Audio Data from ${this.userId}:`, float32Array);
        this.sendData(float32Array);
      };

      this.input.connect(this.processor);
      this.processor.connect(this.audioContext.destination);
    },
    stopCapturing() {
      if (this.processor) {
        this.processor.disconnect();
      }
      if (this.input) {
        this.input.disconnect();
      }
      if (this.stream) {
        this.stream.getTracks().forEach(track => track.stop());
      }
      if (this.audioContext) {
        this.audioContext.close();
      }
    },
    sendData(data) {
      axios.post('http://localhost:5000/api/audio', { userId: this.userId, audioData: Array.from(data) }, {
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        console.log('Data sent successfully:', response.data);
      }).catch(error => {
        console.error('Error sending data:', error);
      });
    }
  },
  mounted() {
    this.joinSession();
  },
  beforeRouteLeave(to, from, next) {
    this.leaveSession();
    next();
  }
};
</script>


<style scoped>
.conference-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  text-align: center;
  padding: 1rem;
  background-color: #ffffff;;
}

.highlight {
  color: blueviolet;
}

.main-content {
  display: flex;
  flex: 1;
  padding: 1rem 0 0 1rem;
  box-sizing: border-box;
}

.center {
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
}

.upper-section {
  display: flex;
  justify-content: space-between;
  /* margin-bottom: 1rem; */
}

/* 참석자 */
.left-side,
.right-side {
  width: 250px;
  background-color: #f3e5f5;
  align-items: center;
  border-radius: 8px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.left-side {
  margin: 0 0 0 1rem;
  border-radius: 8px 8px 0px 8px;
}

.right-side {
  margin: 0 1rem 0 0;
}

.participant {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  padding: 1rem;
  margin: 0.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 220px;
  height: 180px;
}

.participant-video {
  width: 100%;
  height: 80%;
  background-color: #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.participant-info {
  margin-top: 0.5rem;
  text-align: center;
}

/* 발표 */
.presentation {
  flex: 2;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 600px;
  height: 350px;
}

.presentation img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 8px;
}

/* 번역  */
.translation-container {
  display: flex;
  justify-content: space-between;
  border: 2px dashed lightgray;
  border-radius: 8px;
  margin: 1rem;
  font-size: 90%;
}

.translation-section {
  flex: 1;
  padding: 1rem;
}

.original {
  border-right: 2px dashed lightgray;
}

.translation-section:last-child {
  margin-right: 0;
}

.translation-content {
  background-color: #e0e0e0;
  padding: 1rem;
  border-radius: 8px;
}

.message-group {
  margin-bottom: 1rem;
}

.speaker-info {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.speaker-info .language {
  margin-left: 0.5rem;
  font-size: 0.9rem;
  color: #555;
}

.message {
  background-color: #d3d3d3;
  padding: 0.5rem;
  border-radius: 16px;
  margin: 0.5rem 0;
}

.language-icon {
  font-size: 1rem;
  margin-left: 0.5rem;
}

.footer {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  background-color: #f3e5f5;
  margin: 1rem 1rem 0 0;
  border-radius: 0 8px 8px 0;
}

.footer-left,
.footer-center,
.footer-right {
  display: flex;
  flex-direction: column;
}

.bottom-toolbar {
  display: flex;
  justify-content: center;
  padding: 1rem;
  background-color: #ffffff;
}

.bottom-toolbar .btn-icon {
  margin: 0 0.5rem;
}

/* 화상 영역 */

.video-container {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>
