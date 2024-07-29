<template>
    <div class="modal-overlay" @click.self="close">
      <div class="modal-content">
        <h2>{{ user.username }}</h2>
        <p>{{ user.email }}</p>
        <p>Other profile information...</p>
        <button @click="addMember(user)" class="btn btn-primary">추가버튼</button>
        <button @click="close" class="btn btn-secondary">Close</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps, defineEmits, ref } from 'vue';
  import { useUserStore } from '@/stores/userStore';
  import { useTeamStore } from '@/stores/teamStore';
  
  const teamStore = useTeamStore();

  const props = defineProps({
    user: Object
  });

  
  const emits = defineEmits(['close']);
  
  const close = () => {
    emits('close');
  };

  const addMember=(user)=>{
    const teamId= 1;
    console.log(user.id)
    teamStore.addMembertoTeam(user.id,teamId);
    close();
  }

  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 5px;
    width: 400px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  }
  </style>
  