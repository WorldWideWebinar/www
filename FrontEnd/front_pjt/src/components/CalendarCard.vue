<template>
  <div class="card">
    <h2>Meetings on {{ formatDate(selectedDate) }}</h2>
    <ul>
      <li v-for="meeting in meetings" :key="meeting.id">
        <h3>{{ meeting.title }}</h3>
        <p>Host: {{ meeting.host }}</p>
        <p>Participants: {{ meeting.participants.length }}</p>
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import dayjs from 'dayjs';

export default {
  props: {
    meetings: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    const selectedDate = ref(dayjs().format('YYYY-MM-DD'));

    watch(() => props.meetings, (newMeetings) => {
      if (newMeetings.length > 0) {
        selectedDate.value = newMeetings[0].date;
      }
    }, { immediate: true });

    function formatDate(date) {
      return dayjs(date).format('MMMM D, YYYY');
    }

    return {
      selectedDate,
      formatDate
    };
  }
};
</script>

<style scoped>
.card {
  border: 1px solid #ccc;
  padding: 1em;
  margin-top: 1em;
  border-radius: 20px;
  background-color: #fff;
  color: #1d252c;
}
</style>