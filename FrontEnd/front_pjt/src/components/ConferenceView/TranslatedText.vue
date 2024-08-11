<template>

  <h5>Translated Version <span class="language-icon">🌐
    <select v-model="textStore.target_language">
      <option value="en-us">🇺🇸 English</option>
      <option value="ko">🇰🇷 한국어</option>
      <option value="ja">🇯🇵 日本語</option>
      <option value="zh">🇨🇳 中国语</option>
      <option value="es">🇪🇸 Español</option>
    </select>
  </span></h5>
  <div class="translation-content">
    <!-- Translated messages -->
    <div v-for="(script , idx) in textStore.translated" :key="idx"> >
      {{ script }}
    </div>
    <div v-if="textStore.last_received_translated !== null">
       {{textStore.last_received_translated}}
    </div>
  </div>
</template>

<script setup>
import { onUnmounted, ref, watch } from 'vue'
  import { useUserStore } from '@/stores/userStore.js'
  import { useSessionStore } from '@/stores/sessionStore.js'
  import { useSTTStore } from '@/stores/textStore.js'

  const textStore = useSTTStore();
  textStore.target_language= 'ko'
  // const {last_received_segment, transcript} = textStore;
  // textStore.openConnection(token,meetingId);
  // onUnmounted(()=>{
  //   textStore.closeConnection()
  // })
  // const ws = new WebSocket("https://i11a501.p.ssafy.io/api/stomp/stt");
  // const stompClient = Stomp.over(ws)
  //
  // stompClient.connect(
  //   {
  //     Authorization: `Bearer ${token}`
  //   },
  //   function () {
  //     stompClient.subscribe(
  //       `/exchange/meetingSTT.exchange/meetingSTT.key${meetingId}`,
  //       function (message) {
  //         console.log('Received : ', message)
  //         const messageBody = JSON.parse(message.body);
  //         showMessage(messageBody);
  //       }
  //     );
  //   },
  //   function (error) {
  //     console.error("Connection error: " + error);
  //   }
  // );
  // const showMessage = (message) => {
  //   const segments = message["segments"];
  //   processSegments(segments)
  // }
  // let last_segment = null;
  // let last_received_segment = ref(null);
  // const transcript = ref([]);
  //
  // function processSegments(segments) {
  //   for (let i = 0; i < segments.length; i++) {
  //     if(i === segments.length - 1) {
  //       last_segment = segments[i]
  //     } else {
  //       if(transcript.value.length === 0 ||
  //         (segments[i].start >= transcript.value[transcript.value.length - 1].end)){
  //         transcript.value.push({
  //           start : segments[i].start,
  //           end : segments[i].end,
  //           text : segments[i].text,
  //         })
  //       }
  //     }
  //   }
  //   if(last_received_segment.value !== last_segment){
  //     last_received_segment.value = last_segment
  //   }

  // }
</script>


<style scoped>
.translation-content {
  background-color: #e0e0e0;
  padding: 1rem;
  border-radius: 8px;
}

</style>