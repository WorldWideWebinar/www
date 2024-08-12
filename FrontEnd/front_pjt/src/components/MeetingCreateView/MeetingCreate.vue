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
          <label for="startPicker">Start Time</label>
          <div class="time-input-zone">
            <input type="text" v-model="displayStartDate" id="startPicker" required />
            <input type="checkbox" id="startChecked" v-model="startChecked" />
          </div>
        </div>
        <div class="form-group">
          <label for="endPicker">End Time</label>
          <div class="time-input-zone">
            <input type="text" v-model="displayEndDate" id="endPicker" required />
            <input type="checkbox" id="endChecked" v-model="endChecked" />
          </div>
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
import { ref, computed, onMounted } from 'vue';
import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.css';
import { useRoute, useRouter } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { useErrorStore } from '@/stores/errorStore'

const route = useRoute();
const router = useRouter();
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
// const selectedStartDate = ref('');
// const selectedEndDate = ref('');
const displayStartDate = ref('');
const displayEndDate = ref('');
const startChecked = ref(false);
const endChecked = ref(false);

const createMeeting = async () => {
  if (!name.value || !start.value || !end.value) {
    errorStore.showError('필수 필드를 모두 입력해주세요.');
    return;
  }

  if (!startChecked.value || !endChecked.value) {
    alert('Please confirm the start and end times');
    return;
  }

  const newMeeting = {
    team_id: teamId.value,
    name: name.value.replace(/\s+/g, '-'),
    start: start.value,
    end: end.value,
    detail: detail.value,
  };

  try {
    // Backend에 미팅 생성 요청 보내기
    const response = await meetingStore.addMeeting(newMeeting);

    if (response) {
      emits('created', teamId.value)
    }
    close();
  } catch (error) {
    errorStore.showError('미팅 생성 중 오류 발생:', error);
  }
};

const emits = defineEmits(['close','created']);

const close = () => {
  emits('close');
};

const setupFlatpickrStart = () => {
  flatpickr("#startPicker", {
    enableTime: true,
    dateFormat: "Y-m-d\\TH:i",
    time_24hr: true,
    minuteIncrement: 1,
    onChange: (selectedDates) => {
      if (selectedDates.length > 0) {
        const selectedDate = selectedDates[0];
        const timezoneOffset = selectedDate.getTimezoneOffset() * 60000;
        const adjustedDate = new Date(selectedDate.getTime() - timezoneOffset);
        start.value = adjustedDate.toISOString();
        console.log("Start Date for display:", start.value);
        displayStartDate.value = adjustedDate.toISOString().slice(0, 16).replace('T', ' ');
        document.getElementById("startPicker").value = displayStartDate.value;
      }
    }
  });
};

// Setup flatpickr for end date
const setupFlatpickrEnd = () => {
  flatpickr("#endPicker", {
    enableTime: true,
    dateFormat: "Y-m-d\\TH:i",
    time_24hr: true,
    minuteIncrement: 1,
    onChange: (selectedDates) => {
      if (selectedDates.length > 0) {
        const selectedDate = selectedDates[0];
        const timezoneOffset = selectedDate.getTimezoneOffset() * 60000;
        const adjustedDate = new Date(selectedDate.getTime() - timezoneOffset);
        end.value = adjustedDate.toISOString();
        console.log("End Date for display:", end.value);
        displayEndDate.value = adjustedDate.toISOString().slice(0, 16).replace('T', ' ');
        document.getElementById("endPicker").value = displayEndDate.value;
      }
    }
  });
};

onMounted(() => {
  setupFlatpickrStart();
  setupFlatpickrEnd();
})
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

.time-input-zone {
  /* flex-grow:1; */
  display: flex;
  align-items: center;
  gap: 10px; /* 체크박스와 입력 필드 사이의 간격 조정 */
}

.time-input-zone input[type="text"] {
  flex-grow: 1; /* 입력 필드가 가능한 모든 너비를 차지하도록 설정 */
}

.time-input-zone input[type="checkbox"] {
  width: 20px; /* 체크박스 크기 조정 */
  height: 20px; /* 체크박스 크기 조정 */
  margin-left: 10px; /* 체크박스와 입력 필드 사이의 간격 조정 */
}

.time-input-zone input[type="checkbox"]:checked {
  background-color: #007bff; /* 체크된 상태의 배경 색상 조정 */
  border-color: #007bff; /* 체크박스의 경계 색상 조정 */
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
