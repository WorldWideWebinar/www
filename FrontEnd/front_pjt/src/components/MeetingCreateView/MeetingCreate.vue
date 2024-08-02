<template>
  <div class="modal-overlay" @click.self="close">
    <div class="meeting-creation-wrap">
      <h2>Create Meeting</h2>
      <form @submit.prevent="createMeeting">
        <div class="form-group">
          <label for="team">Team</label>
          <div>{{ teamName }}</div>
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
import { useRoute } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';

const route = useRoute();
const teamStore = useTeamStore();
const meetingStore = useMeetingStore();

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
    name: name.value,
    start: new Date(start.value).toISOString(),
    end: new Date(end.value).toISOString(),
    detail: detail.value,
  };

  console.log(newMeeting);

  try {
    await meetingStore.addMeeting(newMeeting);
    alert('Meeting created successfully');
    close();
  } catch (error) {
    console.error('Error creating meeting:', error);
    alert('Error creating meeting');
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
