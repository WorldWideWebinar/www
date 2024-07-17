<template>
  <div v-if="team" class="team-card">
    <h2 class="title">{{ team.name }}</h2>
    <p>{{ team.icon }} 팀장 {{ team.host }}</p>
    <!-- 필요한 경우 더 많은 필드를 추가 -->
     <button class="btn btn-primary">가입 신청</button>
  </div>
</template>

<script setup>
const props = defineProps({
  team: Object
});
</script>

<style scoped>
.team-card {
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