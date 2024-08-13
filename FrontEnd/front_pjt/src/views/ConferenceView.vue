<template>
  <div class="conference-container">
    <header class="header">
      <h3>Welcome to <span class="highlight">{{ departmentName }}</span> Meeting</h3>
    </header>
    <main class="main-content">
      <div class="left-side">
        <user-video v-for="participant in participants" :key="participant.id" :stream-manager="participant.streamManager" />
      </div>
      <div class="center">
        <div class="upper-section">
          <div class="presentation">
            <video id="screen-share-video" autoplay muted style="width: 450px; height: 350px;"></video>
            <!-- <img src="https://via.placeholder.com/450x350" alt="Presentation Screenshot" /> -->
          </div>
          <div class="right-side">
            <user-video class="right-side-video" :stream-manager="myStreamManager" />
          </div>
        </div>
        <div class="translation-container">
          <div class="translation-section original">
            <h5>Original Version</h5>
            <div class="original-content" ref="originalContent">
              <!-- Original messages -->
              <TranscriptionText/>
            </div>
          </div>
          <div class="translation-section translated">
            <TranslatedText/>
          </div>
        </div>
      </div>
    </main>
    <div class="footer">
      <div class="footer-left">
        <span style="font-weight: bold;">Duration</span>
        <span>1:27:31 30min left</span>
      </div>
      <div class="footer-center">
        <span style="font-weight: bold;">Attendance</span>
        <span>{{ participants.length }} / 6</span>
      </div>
      <!-- <div class="footer-right">
        <span>Invite Alex, Joy</span>
      </div> -->
    </div>
    <div class="bottom-toolbar">
      <button class="btn-icon" @click="toggleAudio">{{ isAudioEnabled ? '🔇' : '🎤' }}</button>
      <button class="btn-icon" @click="toggleVideo">{{ isVideoEnabled ? '📷' : '🎥' }}</button>
      <button class="btn-icon" @click="toggleScreenShare">{{ isScreenSharing ? '🛑' : '🖥️' }}</button>
      <button class="btn-icon" @click="leaveSession">🔄</button>
      <button class="btn-icon" @click="endConference">❌</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router';
import { OpenVidu } from 'openvidu-browser';
import { useSessionStore } from '@/stores/sessionStore';
import { useTeamStore } from '@/stores/teamStore';
import { useUserStore } from '@/stores/userStore';
import UserVideo from '@/components/ConferenceView/UserVideo.vue';
import TranscriptionText from '@/components/ConferenceView/TranscriptionText.vue'
import TranslatedText from '@/components/ConferenceView/TranslatedText.vue'

const route = useRoute();
const router = useRouter();
const teamStore = useTeamStore();
const userStore = useUserStore();
const sessionStore = useSessionStore();
const departmentName = computed(() => {
  const teamId = parseInt(route.params.id, 10);
  const teamData = teamStore.getTeamById(teamId);
  return teamData ? teamData.teamName : '';
});

// const sessionId = route.params.sessionId;
const token = route.params.token;;
const session = ref(null);
const publisher = ref(null);
const isAudioEnabled = ref(true);
const isVideoEnabled = ref(true);
const userId = userStore.userId;
const participants = ref([]);
const myStreamManager = ref(null);
const meetingId = sessionStore.meetingId

let socket = null;
let audioContext = null;
let processor = null;
const OV = new OpenVidu();
// const isOwner = computed(() => teamStore.currentTeam?.ownerId === userStore.userId);
const isOwner = computed(() => sessionStore.meetingId != null);
const joinSession = async () => {
  // const OV = new OpenVidu();
  const currentSession = OV.initSession();
  sessionStore.setSession(currentSession);

  // 스트림 생성 이벤트 핸들러
  currentSession.on('streamCreated', (event) => {
    console.log('스트림 생성됨:', event.stream);

    const subscriber = currentSession.subscribe(event.stream, undefined);
    const participantId = JSON.parse(event.stream.connection.data).clientData;

    console.log('구독된 스트림의 참가자 ID:', participantId);

    const participantInfo = userStore.userInfo;
    console.log('참가자 정보:', participantInfo);

    if (participantInfo) {
      participants.value.push({
        id: participantId,
        name: participantInfo.name,
        streamManager: subscriber,
      });
    }

    sessionStore.addStream(subscriber.stream);
  });

  // 스트림 파괴 이벤트 핸들러
  currentSession.on('streamDestroyed', (event) => {
    const participantId = JSON.parse(event.stream.connection.data).clientData;
    participants.value = participants.value.filter(p => p.id !== participantId);
    console.log('스트림이 파괴됨, 참가자 ID:', participantId);
  });

  try {
    await currentSession.connect(token, { clientData: userId });

    // 모든 참가자가 initPublisher를 호출하여 자신의 스트림을 퍼블리싱
    publisher.value = OV.initPublisher(undefined, {
      videoSource: undefined,
      audioSource: undefined,
      publishVideo: true,
      publishAudio: true,
      resolution: '210x140',
      frameRate: 30,
      insertMode: 'APPEND'
    }).on('streamCreated', (event) => {
      if(!isOwner.value) return
      console.log("streamCreated", event);
      let mediaStream
      mediaStream = event.stream.getMediaStream();
      createWebsocketConnection()
      captureAudioStream(mediaStream)
    });
    console.log('publisher stream:', publisher.value.stream)
    currentSession.publish(publisher.value);
    myStreamManager.value = publisher.value;

    // 새 참가자가 기존 스트림 구독
    currentSession.streamManagers.forEach(stream => {
      if (stream.connection.connectionId !== currentSession.connection.connectionId) {
        const subscriber = currentSession.subscribe(stream, undefined);
        const participantId = JSON.parse(stream.connection.data).clientData;

        const participantInfo = userStore.userInfo;
        console.log('참가자 정보:', participantInfo);

        if (participantInfo) {
          participants.value.push({
            id: participantId,
            name: participantInfo.name,
            streamManager: subscriber,
          });
        }

        sessionStore.addStream(subscriber.stream);
        if(isOwner.value) {
          createWebsocketConnection()
          captureAudioStream(subscriber.stream.getMediaStream())
        }
      }
    });

    const team = teamStore.teams.find(team => team.name === route.params.name);
    if (team) {
      participants.value = team.userList.filter(user => user.id !== userId);
    }
  } catch (error) {
    console.error('Error connecting to session:', error);
  }
};
const createWebsocketConnection = ()=>{
  if(socket != null) return

  socket = new WebSocket('wss://i11a501.p.ssafy.io/api/meetingSTT/audio');

  socket.onopen = () => {
    console.log('WebSocket connection opened');
    console.log('Meeting ID:', sessionStore.meetingId);
    socket.send(JSON.stringify({ meetingId: sessionStore.meetingId }));
  };

  socket.onclose = () => {
    console.log('WebSocket connection closed');
  };

  socket.onerror = (error) => {
    console.error('WebSocket error:', error);
    socket.close()
  };
}

const captureAudioStream = (mediaStream) => {
  audioContext = new (window.AudioContext || window.webkitAudioContext)();
  const source = audioContext.createMediaStreamSource(mediaStream);
  processor = audioContext.createScriptProcessor(4096, 1, 1);

  processor.onaudioprocess = (event) => {
    const inputData = event.inputBuffer.getChannelData(0);
    const resampledData = resampleTo16kHz(inputData, audioContext.sampleRate);
    sendDataToBackend(resampledData);
  };
  source.connect(processor);
  processor.connect(audioContext.destination);
};

const resampleTo16kHz = (audioData, originalSampleRate) => {
  const data = new Float32Array(audioData);
  const targetSampleRate = 16000;
  const resampledLength = Math.round(data.length * targetSampleRate / originalSampleRate);
  const resampledData = new Float32Array(resampledLength);

  for (let i = 0; i < resampledLength; i++) {
    const index = i * originalSampleRate / targetSampleRate;
    const intIndex = Math.floor(index);
    const frac = index - intIndex;
    resampledData[i] = data[intIndex] + frac * (data[intIndex + 1] - data[intIndex]);
  }

  return resampledData;
};

const sendDataToBackend = (data) => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    // const audioBuffer = new Int16Array(data.length);
    // for (let i = 0; i < data.length; i++) {
    //   audioBuffer[i] = data[i] * 0x7FFF; // Convert to 16-bit PCM
    // }
    // socket.send(audioBuffer.buffer);  // send the ArrayBuffer representation of the Int16Array
    console.log('sending data');
    socket.send(data.buffer)
  } else {
    console.error('WebSocket is not open');
  }
};

const leaveSession = async () => {
  if (session.value) {
    console.log(meetingId)
    session.value.disconnect();
    socket.close()
    session.value = null;
    const teamId = await sessionStore.getTeamId(sessionStore.meetingId);
      await router.replace({ name: 'ReadyView', params: {id : teamId}  }).catch(err => {
        console.error('Router error:', err);
      });

  }
};

const endConference = async () => {
  console.log("click!!!");
  if (sessionStore && sessionStore.endSession) {
    console.log(sessionStore);
    try {
      const success = await sessionStore.endSession(sessionStore.meetingId);
      if (success) {
        // session.value.disconnect(); // 유사 로직 백엔드 존재
        session.value = null;
      } else {
        console.error('Failed to end the session on the server.');
      }
      // Ready Page로 이동
      const teamId = await sessionStore.getTeamId(sessionStore.meetingId);
      await router.replace({ name: 'ReadyView', params: {id : teamId}  }).catch(err => {
        console.error('Router error:', err);
      });

    } catch (error) {
      console.error('Error while ending the session:', error);
    }
  }
};

const toggleAudio = () => {
  if (publisher.value) {
    isAudioEnabled.value = !isAudioEnabled.value;
    publisher.value.publishAudio(isAudioEnabled.value);
  }
};

const toggleVideo = () => {
  if (publisher.value) {
    isVideoEnabled.value = !isVideoEnabled.value;
    publisher.value.publishVideo(isVideoEnabled.value);
  }
};

const isScreenSharing = ref(false);
const screenPublisher = ref(null);

const toggleScreenShare = async () => {
  if (!isScreenSharing.value) {
    screenPublisher.value = OV.initPublisher('screen-share-video', {
      videoSource: 'screen',
      publishAudio: true,
      publishVideo: true,
      mirror: false,
    });

    session.value.publish(screenPublisher.value);
    myStreamManager.value = screenPublisher.value; // 화면 공유 스트림을 메인 영역에 표시
    isScreenSharing.value = true;
  } else {
    // 화면 공유 중지
    session.value.unpublish(screenPublisher.value);
    screenPublisher.value = null;
    isScreenSharing.value = false;

    myStreamManager.value = publisher.value; // 원래 스트림을 다시 표시
  }
};

// 오리지널/번역 출력 스크롤 생성
const originalContent = ref(null);

const scrollToBottom = (element) => {
  if (element) {
    element.scrollTop = element.scrollHeight;
  }
};

// Watch for changes in original content
watch(() => participants.value, () => {
  scrollToBottom(originalContent.value);
}, { deep: true });

onMounted(() => {
  scrollToBottom(originalContent.value);
});

onMounted(() => {
  joinSession();
});

onBeforeRouteLeave(async (to, from, next) => {
  await leaveSession();
  next();
});
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
  background-color: #ffffff;
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
  border-radius: 8px 8px 0px 0px;
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
  min-height: 250px;
}

.translation-section {
  flex: 1;
  padding: 1rem;
  min-height: 250px;
}

.original {
  border-right: 2px dashed lightgray;
}

.translation-section:last-child {
  margin-right: 0;
}

.original-content {
  background-color: #e0e0e0;
  padding: 1rem;
  border-radius: 8px;
  height: 200px;
  overflow-y: auto;
}

.original-content::-webkit-scrollbar {
  width: 8px;
}

.original-content::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.original-content::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.original-content::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 4px;
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
  border-radius: 0px 0px 8px 8px;
  margin-left: 36px;
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

