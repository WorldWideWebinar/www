<template>
  <div>
    <ul class="nav nav-tabs">
      <li class="nav-item" @click="selectTab('PREV')">
        <a :class="{ 'nav-link': true, active: activeTab === 'PREV' }" aria-current="page" href="#">PREV</a>
      </li>
      <li class="nav-item" @click="selectTab('TODAY')">
        <a :class="{ 'nav-link': true, active: activeTab === 'TODAY' }" aria-current="page" href="#">TODAY</a>
      </li>
      <li class="nav-item" @click="selectTab('NEXT')">
        <a :class="{ 'nav-link': true, active: activeTab === 'NEXT' }" aria-current="page" href="#">NEXT</a>
      </li>
    </ul>

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
        <template v-if="activeTab === 'PREV'">
          <tr v-for="meeting in groupedMeetings.PREV" :key="meeting.meeting_id">
            <td>{{ meeting.start_at.split('T')[0] }}</td>
            <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
            <td :class="{
              agenda: true,
              'bold-agenda':
                selectedMeeting &&
                selectedMeeting.meeting_id === meeting.meeting_id
            }" @click="selectMeeting(meeting)">
              {{ meeting.name }}
            </td>
            <td>
              <button :class="buttonClass('PREV', meeting.status)" @click="toggleStatus(meeting)">
                {{ buttonText('PREV', meeting.status) }}
              </button>
            </td>
          </tr>
        </template>
        <template v-if="activeTab === 'TODAY'">
          <tr v-for="meeting in groupedMeetings.TODAY" :key="meeting.meeting_id">
            <td>{{ meeting.start_at.split('T')[0] }}</td>
            <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
            <td :class="{
              agenda: true,
              'bold-agenda':
                selectedMeeting &&
                selectedMeeting.meeting_id === meeting.meeting_id
            }" @click="selectMeeting(meeting)">
              {{ meeting.name }}
            </td>
            <td>
              <button :class="buttonClass('TODAY', meeting.status)" @click="toggleStatus(meeting)">
                {{ buttonText('TODAY', meeting.status) }}
              </button>
            </td>
          </tr>
        </template>
        <template v-if="activeTab === 'NEXT'">
          <tr v-for="meeting in groupedMeetings.NEXT" :key="meeting.meeting_id">
            <td>{{ meeting.start_at.split('T')[0] }}</td>
            <td>{{ meeting.start_at.split('T')[1] }} - {{ meeting.end_at.split('T')[1] }}</td>
            <td :class="{
              agenda: true,
              'bold-agenda':
                selectedMeeting &&
                selectedMeeting.meeting_id === meeting.meeting_id
            }" @click="selectMeeting(meeting)">
              {{ meeting.name }}
            </td>
            <td>
              <button :class="buttonClass('NEXT', meeting.status)" @click="toggleStatus(meeting)">
                {{ buttonText('NEXT', meeting.status) }}
              </button>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  activeTab: {
    type: String,
    default: 'TODAY'
  },
  groupedMeetings: {
    type: Object,
    required: true
  },
  selectedMeeting: {
    type: Object,
    default: null
  },
  toggleStatus: {
    type: Function,
    required: true
  },
  buttonClass: {
    type: Function,
    required: true
  },
  buttonText: {
    type: Function,
    required: true
  },
  selectMeeting: {
    type: Function,
    required: true
  }
});

const emit = defineEmits(['update:activeTab']);

const selectTab = (tab) => {
  emit('update:activeTab', tab);
};
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