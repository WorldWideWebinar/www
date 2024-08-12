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
</script>


<style scoped>
.translation-content {
  background-color: #e0e0e0;
  padding: 1rem;
  border-radius: 8px;
}

</style>