<template>
  <h5>Original Version</h5>
  <div class="original-content" ref="originalContent">
    <div v-for="(script , idx) in textStore.transcript" :key="idx"> >
      {{ script.text }}
    </div>
    <div v-if="textStore.last_received_segment !== null">
        {{textStore.last_received_segment.text}}
    </div>
  </div>

</template>

<script setup>
  import { onUnmounted, ref, watch, onMounted } from 'vue'
  import { useUserStore } from '@/stores/userStore.js'
  import { useSessionStore } from '@/stores/sessionStore.js'
  import { useSTTStore } from '@/stores/textStore.js'

  const userStore = useUserStore();
  const token = userStore.accessToken;
  const sessionStore = useSessionStore();
  const meetingId = sessionStore.sessionId;
  const textStore = useSTTStore();

  textStore.openConnection(token,meetingId);
  onUnmounted(()=>{
    textStore.closeConnection()
  });

  // 오리지널/번역 출력 스크롤 생성
  const originalContent = ref(null);

  const scrollToBottom = () => {
    if (originalContent.value) {
      originalContent.value.scrollTop = originalContent.value.scrollHeight;
    }
  };

  // New translations are added, scroll to the bottom
  watch(() => textStore.transcript, () => {
    scrollToBottom();
  }, { deep: true });

  watch(() => textStore.last_received_segment, () => {
    scrollToBottom();
  });


  onMounted(() => {
    scrollToBottom();
  });


</script>


<style scoped>
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
</style>