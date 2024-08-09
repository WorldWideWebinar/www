<template>
  <div>
    <table class="meeting-list">
      <thead>
        <tr>
          <th>DATE</th>
          <th>TIME</th>
          <th>AGENDA</th>
          <th>JOIN</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="meeting in filteredMeetings" :key="meeting.id">
          <td>{{ meeting.start_at.split('T')[0] }}</td>
          <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
          <td :class="{ agenda: true, 'bold-agenda': selectedMeeting && selectedMeeting.id === meeting.id }"
            @click="$emit('selectMeeting', meeting)">
            {{ meeting.name }}
          </td>
          <td>
            <button :class="buttonClass(meeting.status)" @click="toggleStatus(meeting)">
              {{ buttonText(meeting.status) }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useMeetingStore } from '@/stores/meetingStore';

const props = defineProps({
  activeTab: String,
});

const meetingStore = useMeetingStore();

const filteredMeetings = computed(() => {
  const now = new Date();
  if (props.activeTab === 'PREV') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.end_at) < now);
  } else if (props.activeTab === 'TODAY') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.start_at) <= now && new Date(meeting.end_at) >= now);
  } else if (props.activeTab === 'NEXT') {
    return meetingStore.meetings.filter(meeting => new Date(meeting.start_at) > now);
  }
  return [];
});

const toggleStatus = (meeting) => {
  meeting.status = meeting.status === 'IN' ? 'OUT' : 'IN';
};

const buttonClass = (status) => {
  return status === 'IN' ? 'btn-green' : 'btn-red';
};

const buttonText = (status) => status;
</script>

<style scoped>
ul.nav-tabs .nav-item .nav-link {
  color: black;
}

ul.nav-tabs .nav-item .nav-link:hover,
ul.nav-tabs .nav-item .nav-link.active {
  font-weight: bolder;
}

ul.nav {
  width: 100%;
}

ul.nav li {
  width: 33.33%;
  text-align: center;
}

.meeting-list {
  width: 102.5%;
  border-collapse: collapse;
  margin: 20px 0 0 0;
  font-size: 95%;
}

.meeting-list th,
.meeting-list td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: center;
}

.meeting-list th:nth-child(1),
.meeting-list td:nth-child(1) {
  width: 20%;
}

.meeting-list th:nth-child(2),
.meeting-list td:nth-child(2) {
  width: 20%;
}

.meeting-list th:nth-child(3),
.meeting-list td:nth-child(3) {
  width: auto;
}

.meeting-list th:nth-child(4),
.meeting-list td:nth-child(4) {
  width: 20%;
}

.meeting-list td:nth-child(4):hover,
.meeting-list td:nth-child(4):hover *,
.meeting-list td.agenda:hover {
  font-weight: bold;
  cursor: pointer;
}

.meeting-list td:nth-child(3):hover,
.meeting-list td:nth-child(3):hover *,
.meeting-list td.agenda:hover {
  cursor: pointer;
  text-decoration-line: underline;
  text-decoration-style: line;
  text-decoration-color: rgba(154, 130, 253, 0.4);
  text-decoration-thickness: 3px;
}

.meeting-list .bold-agenda {
  font-weight: bolder;
  color: rgb(154, 130, 253);
}

.meeting-list th {
  background-color: #f5f5f5;
  font-weight: bold;
}

td button {
  border: none;
  border-radius: 50px;
  width: 60px;
  padding: 5px;
  cursor: pointer;
  text-align: center;
}

button {
  border: none;
  cursor: pointer;
  text-align: center;
}

.btn-green {
  background-color: rgba(139, 195, 74, 0.5);
  color: black;
}

.btn-red {
  background-color: rgba(244, 67, 54, 0.5);
  color: black;
}

.btn-gray {
  background-color: rgba(108, 117, 125, 0.5);
  color: black;
}

.meeting-list tbody {
  display: block;
  max-height: 140px;
  overflow-y: scroll;
}

.meeting-list tbody::-webkit-scrollbar {
  width: 0;
  background: transparent;
}

.meeting-list tr {
  display: table;
  width: calc(100% - 1rem);
  table-layout: fixed;
}


</style>