<template>
  <div>
    <h1 v-if="!hasChildRoute">{{ departmentName }} 부서 미팅 리스트</h1>
    <button v-if="!hasChildRoute" @click="joinConference">지금 참여하세요!</button>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'MeetingList',
  computed: {
    hasChildRoute() {
      return this.$route.path !== `/${this.$route.params.name}`;
    },
    departmentName() {
      return this.$route.params.name;
    }
  },
  methods: {
    joinConference() {
      this.$router.push({ name: 'ConferenceView', params: { name: this.departmentName } });
    }
  }
};
</script>

<style scoped>
/* 스타일 추가 */
</style>
