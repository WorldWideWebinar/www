<template>
  <div class="modal-overlay" @click.self="close">
    <div class="meeting-creation-wrap">
      <h2>Create Meeting</h2>
      <form @submit.prevent="createMeeting">
        <div class="form-group">
          <label for="team">Team</label>
          <input type="text" v-model="teamName" id="team" readonly />
        </div>
        <div class="form-group">
          <label for="name">Meeting Name</label>
          <input type="text" v-model="name" id="name" required />
        </div>
        <div class="form-group">
          <label for="start">Start Time</label>
          <input type="datetime-local" v-model="start" id="start" required />
        </div>
        <div class="form-group">
          <label for="end">End Time</label>
          <input type="datetime-local" v-model="end" id="end" required />
        </div>
        <div class="form-group">
          <label for="detail">Details</label>
          <textarea v-model="detail" id="detail"></textarea>
        </div>
        <div class="button-group">
          <button type="submit" class="btns btn-create">Create Meeting</button>
        </div>
      </form>
      <button @click="close" class="btns btn-close"></button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';

const route = useRoute();
const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const errorStore = useErrorStore();
const teamId = computed(() => parseInt(route.params.id, 10));
const teamName = computed(() => {
  const team = teamStore.teams.find(team => team.id === teamId.value);
  return team ? team.teamName : '';
});

const name = ref('');
const start = ref('');
const end = ref('');
const detail = ref('');

const createMeeting = async () => {
  if (!name.value || !start.value || !end.value) {
    alert('Please fill out all required fields');
    return;
  }

  const newMeeting = {
    team_id: teamId.value,
    name: name.value.replace(/\s+/g, '-'),
    start: new Date(start.value).toISOString(),
    end: new Date(end.value).toISOString(),
    detail: detail.value,
  };

  console.log(newMeeting);

  try {
    await meetingStore.addMeeting(newMeeting);
    close();
  } catch (error) {
    errorStore.showError('Error creating meeting:', error);
  }
};

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
}

.meeting-creation-wrap {
  width: 100%;
  max-width: 600px;
  margin: 0 20px;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #007bff;
  outline: none;
}

.form-group input[readonly] {
  background-color: #f9f9f9;
  color: #555;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #cccccc;
  outline: none;
}

.button-group {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.btns {
  border: none;
  text-align: center;
  cursor: pointer;
}

.btn-create {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-weight: bold;
  padding: 12px 35px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.btn-create:hover {
  background-color: #b380bc;
}

.btn-close {
  background-color: #6c757d;
  color: #fff;
  font-size: x-small;
  position: absolute;
  top: 25px;
  right: 20px;
  padding: 10px;
  outline: none;
}

.btn-close:hover {
  background-color: #5a6268;
}

.btn-close:focus,
.btn-close:active {
  outline: none;
  border: none;
  box-shadow: none;
}
</style>
