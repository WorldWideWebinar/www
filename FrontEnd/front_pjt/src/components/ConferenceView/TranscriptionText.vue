<template>
  <div v-for="(idx , script) in transcript" :key="idx"> >
    {{ script.text }}
  </div>
</template>

<script setup>
  import { ref } from 'vue'
  import { useUserStore } from '@/stores/userStore.js'
  import { useSessionStore } from '@/stores/sessionStore.js'

  const userStore = useUserStore();
  const token = userStore.accessToken;
  const sessionStore = useSessionStore();
  const meetingId = sessionStore.meetingId;

  const ws = new WebSocket("https://i11a501.p.ssafy.io/api/stomp/stt");
  const stompClient = Stomp.over(ws)

  stompClient.connect(
    {
      Authorization: `Bearer ${token}`
    },
    function () {
      stompClient.subscribe(
        `/exchange/meetingSTT.exchange/meetingSTT.key${meetingId}`,
        function (message) {
          console.log('Received : ', message)
          const messageBody = JSON.parse(message.body);
          showMessage(messageBody);
        }
      );
    },
    function (error) {
      console.error("Connection error: " + error);
    }
  );
  const showMessage = (message) => {
    const meetingID = message["meetingId"];
    const content = message["content"];
    const segments = message["segments"];
    processSegments(segments)
  }
  let text = [];
  let last_segment = null;
  let last_received_segment = null;
  let last_response_received = null;
  const transcript = ref([]);
  const server_backend = "faster_whisper";  // 서버 백엔드 설정

  function processSegments(segments) {
    segments.forEach((seg, i) => {
      if (text.length === 0 || text[text.length - 1] !== seg.text) {
        text.push(seg.text);
        if (i === segments.length - 1) {
          last_segment = seg;
        } else if (server_backend === "faster_whisper" &&
          (!transcript.value.length ||
            parseFloat(seg.start) >= parseFloat(transcript[transcript.value.length - 1].end))) {
          transcript.value.push(seg);
        }
      }
    });

    // 마지막으로 수신된 세그먼트와 시간을 업데이트
    if (last_received_segment === null || last_received_segment !== segments[segments.length - 1].text) {
      last_response_received = Date.now();  // 현재 시간 (밀리초 단위)
      last_received_segment = segments[segments.length - 1].text;
    }
  }
</script>


<style scoped>

</style>