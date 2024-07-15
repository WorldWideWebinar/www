<template>
  <div>
    <h1 v-if="!hasChildRoute">Meeting List</h1>
    <!-- 추가 내용 -->
    <button v-if="!hasChildRoute" @click="joinConference">지금 참여하세요!</button>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'MeetingList',
  computed: {
    hasChildRoute() {
      return this.$route.path !== '/rnd'
    }
  },
  methods: {
    joinConference() {
      this.$router.push({ name: 'ConferenceView' })
    }
  }
}
</script>

<style scoped>
/* 스타일 추가 */
</style>
