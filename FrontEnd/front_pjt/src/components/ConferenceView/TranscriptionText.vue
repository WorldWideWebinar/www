<template>
  <div v-for="(script , idx) in textStore.transcript" :key="idx"> >
    {{ script.text }}
  </div>
  <div v-if="textStore.last_received_segment !== null">
      {{textStore.last_received_segment.text}}
  </div>

</template>

<script setup>
import { onUnmounted, ref } from 'vue'
  import { useUserStore } from '@/stores/userStore.js'
  import { useSessionStore } from '@/stores/sessionStore.js'
  import { useSTTStore } from '@/stores/textStore.js'

  const userStore = useUserStore();
  const token = userStore.accessToken;
  const sessionStore = useSessionStore();
  const meetingId = sessionStore.meetingId;
  const textStore = useSTTStore();
  textStore.openConnection(token,meetingId);
  onUnmounted(()=>{
    textStore.closeConnection()
  })

</script>


<style scoped>

</style>