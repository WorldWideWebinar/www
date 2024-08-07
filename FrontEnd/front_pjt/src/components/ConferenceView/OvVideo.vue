<template>
  <video ref="videoEl" autoplay />
</template>
  
<script>
export default {
  name: 'OvVideo',
}
</script>

<script setup>
import { ref, onMounted } from "vue";

const props = defineProps({
  streamManager: Object,
});

const videoEl = ref(null);

onMounted(() => {
  if (props.streamManager && videoEl.value) {
    props.streamManager.addVideoElement(videoEl.value);
  }
});
</script>
