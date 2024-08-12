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
          <input type="text" v-model="displayStartDate" id="startPicker" required />
          <input type="checkbox" id="startChecked" v-model="startChecked" />
        </div>
        <div class="form-group">
          <label for="endPicker">End Time</label>
          <input type="text" v-model="displayEndDate" id="endPicker" required />
          <input type="checkbox" id="endChecked" v-model="endChecked" />
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
import { useRoute } from 'vue-router';
import { useTeamStore } from '@/stores/teamStore';
import { useMeetingStore } from '@/stores/meetingStore';
import { useErrorStore } from '@/stores/errorStore'

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
// const selectedStartDate = ref('');
// const selectedEndDate = ref('');
const displayStartDate = ref('');
const displayEndDate = ref('');
const startChecked = ref(false);
const endChecked = ref(false);

const createMeeting = async () => {
  if (!name.value || !start.value || !end.value) {
    alert('필수 필드를 모두 입력해주세요.');
    return;
  }

  if (!startChecked.value || !endChecked.value) {
    alert('Please confirm the start and end times');
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
    // Backend에 미팅 생성 요청 보내기
    const response = await meetingStore.addMeeting(newMeeting);

    if (response) {
      // 응답을 받은 후 start, end를 start_at, end_at으로 변경
      const savedMeeting = {
        ...newMeeting,
        meeting_id: response.data.result,
        start_at: newMeeting.start,
        end_at: newMeeting.end,
      };
      delete savedMeeting.start;
      delete savedMeeting.end;

      meetingStore.meetings.push(savedMeeting); // 수정된 미팅을 store에 추가
    }
    
    close();
  } catch (error) {
    errorStore.showError('미팅 생성 중 오류 발생:', error);
  }
};

const emits = defineEmits(['close']);

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
        start.value = selectedDate.toISOString().slice(0, 16);
        console.log("Start Date for display:", start.value);
        const timezoneOffset = selectedDate.getTimezoneOffset() * 60000; // 분 단위의 오프셋을 밀리초로 변환
        const adjustedDate = new Date(selectedDate.getTime() - timezoneOffset);
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
        end.value = selectedDate.toISOString().slice(0, 16);
        console.log("End Date for display:", end.value);
        const timezoneOffset = selectedDate.getTimezoneOffset() * 60000;
        const adjustedDate = new Date(selectedDate.getTime() - timezoneOffset);
        displayStartDate.value = adjustedDate.toISOString().slice(0, 16).replace('T', ' ');
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
