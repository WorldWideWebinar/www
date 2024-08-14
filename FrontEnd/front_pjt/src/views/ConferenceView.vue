<template>
  <div class="conference-container">
    <header class="header">
      <span>Welcome to <span class="highlight">{{ conferenceName }}</span> Meeting Page</span>
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
            <div class="my-toolbar">
              <button class="btn-icon" @click="toggleAudio">
                <img :src="isAudioEnabled ? audioOnIcon : audioOffIcon" alt="Toggle Audio" />
              </button>
              <button class="btn-icon" @click="toggleVideo">
                <img :src="isVideoEnabled ? videoOnIcon : videoOffIcon" alt="Toggle Video" />
              </button>
              <button class="btn-icon" @click="toggleScreenShare">
                <img :src="isScreenSharing ? screenOnIcon : screenOffIcon" alt="Toggle Screen Share" />
              </button>
            </div>
          </div>
        </div>
        <div class="translation-container">
          <div class="translation-section original">
              <!-- Original messages -->
              <TranscriptionText/>
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
        <span>{{ attendedNums.length }}</span>
      </div>
      <div class="footer-center">
        <span style="font-weight: bold;">Attendance</span>
        <span>{{ attendedNums.length }} / {{ participants.length }}</span>
      </div>
      <!-- <div class="footer-right">
        <span>Invite Alex, Joy</span>
      </div> -->
      <div class="footer-right">
        <span style="font-weight: bold;">Exit</span>
        <div class="bottom-toolbar">
          <button class="btn-icon" @click="leaveSession"><img src="../assets/img/refresh.png" alt="refresh"></button>
          <button class="btn-icon" @click="endConference"><img src="../assets/img/end.png" alt="end"></button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, watch, computed, onMounted, onUnmounted } from 'vue';
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
const conferenceName = computed(() => {
  return sessionStore.meetingInfo ? sessionStore.meetingInfo.name : '';
});

const attendedNums = computed(() => {
  return sessionStore.participants ? sessionStore.participants : '';
});

// const sessionId = route.params.sessionId;
const token = route.params.token;;
const session = computed(() => sessionStore.session);
const publisher = ref(null);
const isAudioEnabled = ref(true);
const isVideoEnabled = ref(true);
const userId = userStore.userId;
const userName = userStore.userInfo.name
const participants = ref([]);
const myStreamManager = ref(null);
const meetingId = computed(() => sessionStore.sessionId)

// 이미지 경로 지정
import audioOffIcon from '@/assets/img/audio_off.png';
import audioOnIcon from '@/assets/img/audio_on.png';
import videoOffIcon from '@/assets/img/video_off.png';
import videoOnIcon from '@/assets/img/video_on.png';
import screenOnIcon from '@/assets/img/screen_off.png';
import screenOffIcon from '@/assets/img/screen_on.png';


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
  console.log("Team Owner? : ", isOwner.value)
  currentSession.on('streamCreated', (event) => {

    const subscriber = currentSession.subscribe(event.stream, undefined);
    const participantId = JSON.parse(event.stream.connection.data).clientData;

    const participantInfo = userStore.userInfo;

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
    await currentSession.connect(token, { clientData: userName });

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
      let mediaStream
      mediaStream = event.stream.getMediaStream();
      createWebsocketConnection()
      captureAudioStream(mediaStream)
    }).on('streamAudioVolumeChange', () => {

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
  socket = new WebSocket('wss://i11a501.p.ssafy.io/api/meetingSTT/audio');

  socket.onopen = () => {
    console.log('WebSocket connection opened');
    console.log('Meeting ID:', sessionStore.sessionId);
    socket.send(JSON.stringify({ meetingId: sessionStore.sessionId.toString() }));
  };

  socket.onclose = () => {
    console.log('WebSocket connection closed');
  };

  socket.onerror = (error) => {
    console.error('WebSocket error:', error);
    socket.close()
  };
}

audioContext = new (window.AudioContext || window.webkitAudioContext)();
const captureAudioStream = (mediaStream) => {
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
    // const teamId = await sessionStore.getTeamId(sessionStore.sessionId);
    //   await router.replace({ name: 'ReadyView', params: {id : teamId}  }).catch(err => {
    //     console.error('Router error:', err);
    //   });

  session.value.disconnect();
  socket.close()
  console.log('meetingId', meetingId)
  session.value = null;
  const teamId = await sessionStore.getTeamId(meetingId);
  router.replace({ name: 'ReadyView', params: {id : teamId} })
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
      console.log('TeamID', teamId)
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
    if(!isAudioEnabled.value){
      processor.disconnect()
      socket.close();
    }else {
      createWebsocketConnection()
      captureAudioStream(publisher.value.stream.getMediaStream())
    }
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
    screenPublisher.value = OV.initPublisher(undefined, {
      videoSource: 'screen',
      publishAudio: true,
      publishVideo: true,
      mirror: false,
    });

    session.value.publish(screenPublisher.value);
    screenPublisher.value.addVideoElement(document.getElementById('screen-share-video'));
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

onBeforeRouteLeave(async (to, from, next) => {
  await leaveSession();
  next();
});

onMounted(async () => {
  const meetingId = sessionStore.meetingId;
  if (meetingId) {
    await sessionStore.fetchMeetingById(meetingId);  // 미팅 정보 가져오기
  }
  await joinSession();
});

onUnmounted(() => {
  // Close the WebSocket connection if it exists
  if (socket) {
    socket.close();
  }

  // Stop the audio processor and disconnect it from the audio context
  if (processor && audioContext) {
    processor.disconnect();
    processor.onaudioprocess = null; // Remove the event handler
    audioContext.close();
  }

  // Stop the media stream tracks if they are still active
  if (publisher.value) {
    const mediaStream = publisher.value.stream.getMediaStream();
    if (mediaStream) {
      mediaStream.getTracks().forEach(track => track.stop());
    }
  }
  if (session.value) {
    session.value.disconnect();
    sessionStore.clearSession(); // Assuming you have a method to clear the session data
  }

  // Reset relevant refs
  participants.value = [];
  myStreamManager.value = null;
  screenPublisher.value = null;
  isScreenSharing.value = false;
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
  padding: 1rem 0 1.5rem 0;
  font-weight: bolder;
  font-size: xx-large;
}

.highlight {
  color: rgb(166, 125, 247);
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
  padding: 2rem 2rem 0 2rem;
  display: flex;
  flex-direction: column;
  margin: auto;
}

.left-side {
  margin: 0 0 0 1rem;
  border-radius: 8px 8px 0px 0px;
}

.right-side {
  margin: 0 1rem 0 0;
}

.my-toolbar {
  display: flex;
  justify-content: center;
  padding: 1rem 1rem 0 1rem;
  margin-top: 20px;
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
  padding-bottom: 0rem;
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

.footer-left {
  margin-left: 260px;
}

.footer-center {
  /* margin-right: 100px; */
}

.bottom-toolbar {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-right: 100px;
  background-color: #ffffff;
}

.btn-icon {
  margin: 0.5rem 1rem;
  background-color: #c5c5c5;
  border: lightgray;
  width: 40px;
  height: 40px;
  border-radius: 10px;
}

.btn-icon:hover {
  background-color: #a6a6a6;
}

.btn-icon img {
  width: 30px;
  height: 30px;
  margin: auto;
}

/* 화상 영역 */

.video-container {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>

