<template>
  <div class="card">
    <h3 class="datetime">{{ formatDate(selectedDate) }}</h3>
    <ul>
      <li v-for="meeting in meetings" :key="meeting.id">
        <h2 class="title">{{ meeting.title }}</h2>
        <p class="host">Host: {{ meeting.host }}</p>
        <p class="participants">Participants: {{ meeting.participants.length }}</p>
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
* {
  box-sizing: border-box;
  font-family: 'Montserrat', sans-serif;
  font-weight: 500;
}


ul{
  list-style:none;
}

.card {
  border: 1px solid #ccc;
  padding: 1em;
  margin-top: 1em;
  border-radius: 20px;
  background-color: #fff;
  color: #1d252c;
}

.datetime {
  text-align: center; 
}

.title {
  text-align: center;
}
</style>