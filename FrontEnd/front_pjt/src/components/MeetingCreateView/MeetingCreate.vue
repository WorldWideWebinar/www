<template>
  <div class="modal-overlay" @click.self="close">
    <div class="meeting-creation-wrap">
      <h2>Create Meeting</h2>
      <form @submit.prevent="createMeeting">
        <!-- 위에 팀만 수정하면 됨-->
        <div class="form-group">
          <label for="team">Team</label>
          <!-- <select v-model="teamId" required>
            <option v-for="team in teams" :key="team.id" :value="team.id">{{ team.teamName }}</option>
          </select> -->
          <div>{{ propedTeamId }}</div>
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
        <div>
          <button type="submit" class="btn btn-primary">Create Meeting</button>
        </div>
      </form>
      <button @click="close" class="btn btn-secondary">Close</button>
    </div>
  </div>
  </template>

<script setup>
import { ref, computed } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { defineProps } from 'vue';
import axios from 'axios';

const teamStore = useTeamStore();
const meetingStore = useMeetingStore();
const teams = computed(() => teamStore.teams);


const name = ref('');
const start = ref('');
const end = ref('');
const detail = ref('');

const props = defineProps({
  propedTeamId: {
    type: Number,
    required: true
  }
});

const teamId = ref(props.propedTeamId);

const createMeeting = async () => {

  if (!teamId.value || !name.value || !start.value || !end.value) {
    alert('Please fill out all required fields');
    return;
  }

  // teamId = ref(1);
  const newMeeting = {
    team_id: teamId.value,
    name: name.value,
    start: new Date(start.value).toISOString(),
    end: new Date(end.value).toISOString(),
    detail: detail.value,
  };

  console.log(newMeeting)

  // meetingStore.addMeeting(newMeeting);

  close();
  // try {
  //   const response = await axios.post('http://localhost:5000/api/meetings', newMeeting);
  //   if (response.data.isSuccess) {
  //     alert('Meeting created successfully');
  //     // Reset fields
  //     teamId.value = null;
  //     name.value = '';
  //     start.value = '';
  //     end.value = '';
  //     detail.value = '';
  //   } else {
  //     console.error('Failed to create meeting:', response.data.message);
  //     alert('Failed to create meeting');
  //   }
  // } catch (error) {
  //   console.error('Error creating meeting:', error);
  //   alert('Error creating meeting');
  // }
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
  /* max-width: 1200px; */
  min-width: 600px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #fff;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: #fff;
}
</style>
