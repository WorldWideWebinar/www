<template>
    <div class="modal-overlay" @click="close">
      <div class="modal" @click.stop>
        <button class="close-button" @click="close">&times;</button>
        <div class="modal-content">
          <h3>프로필</h3>
          <p>사용자 이름: {{ user.username }}</p>
          <p>이메일: {{ user.email }}</p>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    user: Object
  });
  
  const emits = defineEmits(['close']);
  
  const close = () => {
    emits('close');
  };
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
    z-index: 1000;
  }
  
  .modal {
    background: #fff;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: relative;
    width: 80%;
    max-width: 500px;
  }
  
  .close-button {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
  }
  
  .modal-content {
    margin-top: 20px;
  }
  </style>