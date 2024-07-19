<template>
  <div>
    <UserSearch />
  </div>
</template>

<script setup>
import UserSearch from '@/components/UserSearch.vue';
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
</script>

<style scoped>

</style>