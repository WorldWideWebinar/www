<template>
  <div v-if="user" class="user-card">
    <h2 class="title">{{ user.username }}</h2>
    <p>{{ user.email }}</p>
    <button class="btn btn-primary" @click="showProfileModal = true">프로필 보기</button>
    <button class="btn btn-primary" style="float: right;">Add</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  user: Object
});
const showProfileModal = ref(false);
</script>

<style scoped>
.user-card {
  border: 1px solid #e1bee7;
  border-radius: 5px;
  padding: 20px;
  background: #fff;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.title {
  text-align: center;
}
</style>
