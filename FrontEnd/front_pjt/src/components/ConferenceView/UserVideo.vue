<template>
  <div v-if="streamManager" class="stream">
    <ov-video :stream-manager="streamManager" ref="videoContainer"/>
    <!-- {{ clientData }}이게 내 현재 이름임 -->
    <div class="profile">{{ clientData }}</div>
  </div>
</template>

<script>
  export default {
    name: 'UserVideo',
  }
</script>

<script setup>
import { computed, onMounted, ref } from 'vue';
import OvVideo from '@/components/ConferenceView/OvVideo.vue';

const props = defineProps({
  streamManager: Object,
});

const videoContainer = ref(null);

const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});

function getConnectionData() {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
}

onMounted(() => {
  if (videoContainer.value) {
    console.log('Video container is successfully created:', videoContainer.value);
  } else {
    console.log('Video container creation failed');
  }
});
</script>

<style scoped>
.stream {
  margin: 12px 0px;
}

.profile {
  text-align: center;
  font-weight: bold;
}
</style>