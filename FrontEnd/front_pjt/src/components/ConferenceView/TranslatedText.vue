<template>
  <h5>Translated Version
    <select class="language-choice" v-model="textStore.target_language">
      <option value="en-us">🇺🇸 &nbsp;English</option>
      <option value="ko">🇰🇷 &nbsp;한국어</option>
      <option value="ja">🇯🇵 &nbsp;日本語</option>
      <option value="zh">🇨🇳 &nbsp;中国语</option>
      <option value="es">🇪🇸 &nbsp;Español</option>
      <option value="fr">🇫🇷 &nbsp;Français</option>
      <option value="de">🇩🇪 &nbsp;Deutsche</option>
    </select>
  </h5>
  <div class="translated-content" ref="translatedContent">
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
  import { onUnmounted, ref, watch, onMounted } from 'vue'
  import { useUserStore } from '@/stores/userStore.js'
  import { useSessionStore } from '@/stores/sessionStore.js'
  import { useSTTStore } from '@/stores/textStore.js'

  const textStore = useSTTStore();
  textStore.target_language= 'ko';

  const translatedContent = ref(null);

  const scrollToBottom = () => {
    if (translatedContent.value) {
      translatedContent.value.scrollTop = translatedContent.value.scrollHeight;
    }
  };

  // New translations are added, scroll to the bottom
  watch(() => textStore.translated, () => {
    scrollToBottom();
  }, { deep: true });

  watch(() => textStore.last_received_translated, () => {
    scrollToBottom();
  });

  // Ensure the content is scrolled to the bottom on mount
  onMounted(() => {
    scrollToBottom();
  });
</script>


<style scoped>
select {
  font-size: medium;
}

.translated-content {
  background-color: #e0e0e0;
  padding: 1rem;
  border-radius: 8px;
  height: 200px;
  overflow-y: auto;
}

.translated-content::-webkit-scrollbar {
  width: 8px;
}

.translated-content::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.translated-content::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.translated-content::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  border-radius: 4px;
}

select {
  /* -moz-appearance: none;
  -webkit-appearance: none; */
  /* appearance: none; */
  margin-left: 20px;
  font-weight: bold;
}

.language-choice {
  width: 150px;
  border: none;
  padding: 5px 10px;
}

</style>