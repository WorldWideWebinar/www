<template>
  <div class="cal-container">
    <div class="cal-content">
      <h3>Calendar</h3>
      <div id="calendar">
        <div class="calendar-header">
          <button @click="previousMonth" class="nav-button">Previous</button>
          <h2>{{ currentMonth.format('MMMM YYYY') }}</h2>
          <button @click="nextMonth" class="nav-button">Next</button>
        </div>
        <div class="calendar">
          <div class="calendar-row calendar-header-row">
            <div class="calendar-cell" v-for="day in daysOfWeek" :key="day">{{ day }}</div>
          </div>
          <div class="calendar-row" v-for="week in calendarDays" :key="week[0].date">
            <div class="calendar-cell"
                 v-for="day in week"
                 :key="day.date"
                 :class="{ 'hasMeeting': hasMeeting(day.date), 'currentMonth': day.currentMonth, 'selected': isSelected(day) }"
                 @click="selectDay(day)">
              {{ day.date.format('D') }}
            </div>
          </div>
        </div>
        <button @click="openModal" class="add-event-button">Add Event</button>
      </div>
      <CalendarCard v-if="selectedMeetings.length" :meetings="selectedMeetings" />
      <EventModal :isOpen="isModalOpen" @close="closeModal" @eventCreated="addEvent" />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import CalendarCard from './CalendarCard.vue';
import EventModal from './EventModal.vue';
import dayjs from 'dayjs';

export default {
  components: { CalendarCard, EventModal },
  setup() {
    const userStore = useUserStore();
    const currentMonth = ref(dayjs().startOf('month'));
    const selectedDate = ref(null);
    const isModalOpen = ref(false);

    const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

    const calendarDays = computed(() => {
      const startOfMonth = currentMonth.value.startOf('month');
      const endOfMonth = currentMonth.value.endOf('month');
      const startDate = startOfMonth.startOf('week');
      const endDate = endOfMonth.endOf('week');
      
      const days = [];
      let date = startDate;
      
      while (date.isBefore(endDate, 'day')) {
        const week = [];
        for (let i = 0; i < 7; i++) {
          week.push({
            date,
            currentMonth: date.isSame(currentMonth.value, 'month')
          });
          date = date.add(1, 'day');
        }
        days.push(week);
      }
      return days;
    });

    const selectedMeetings = computed(() => {
      if (!selectedDate.value) return [];
      return userStore.meetings.filter(meeting =>
        meeting.date === selectedDate.value.format('YYYY-MM-DD')
      );
    });

    onMounted(() => {
      // 백엔드 구축 후 주석 해제
      // userStore.fetchUserSessions('userId'); // 적절한 userId로 변경하세요.
    });

    function selectDay(day) {
      selectedDate.value = day.date;
    }

    function isSelected(day) {
      return selectedDate.value && selectedDate.value.isSame(day.date, 'day');
    }

    function hasMeeting(date) {
      return userStore.meetings.some(meeting => meeting.date === date.format('YYYY-MM-DD'));
    }

    function previousMonth() {
      currentMonth.value = currentMonth.value.subtract(1, 'month');
    }

    function nextMonth() {
      currentMonth.value = currentMonth.value.add(1, 'month');
    }

    function openModal() {
      isModalOpen.value = true;
    }

    function closeModal() {
      isModalOpen.value = false;
    }

    function addEvent(newEvent) {
      userStore.addMeeting(newEvent);
    }

    return {
      daysOfWeek,
      calendarDays,
      selectedDate,
      selectedMeetings,
      selectDay,
      isSelected,
      hasMeeting,
      previousMonth,
      nextMonth,
      openModal,
      closeModal,
      addEvent,
      isModalOpen,
      currentMonth
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

body {
  padding: 0;
  margin: 0;
}

.cal-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  height: 100vh;
}

.cal-content {
  background: #1d252c;
  color: #fff;
  padding: 40px 60px;
  text-align: center;
  margin-right: 20px;
  border-radius: 20px;
}

.cal-content h3 {
  font-size: 37px;
  font-weight: 900;
  margin: 0 0 1rem;
}

#calendar {
  display: inline-block;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.nav-button {
  background-color: #3dae2b;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
}

.nav-button:hover {
  background-color: #218838;
}

.calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.calendar-row {
  display: contents;
}

.calendar-cell {
  padding: 10px;
  text-align: center;
  border-radius: 10px;
  cursor: pointer;
  background-color: #343f48;
  transition: background-color 0.3s;
}

.calendar-header-row .calendar-cell {
  font-weight: bold;
  background-color: #343f48;
}

.calendar-cell.currentMonth {
  background-color: #1d252c;
}

.calendar-cell:not(.currentMonth) {
  background-color: #2a2f36;
}

.calendar-cell.hasMeeting {
  font-weight: bold;
  color: #3dae2b;
}

.calendar-cell.selected {
  background-color: #3dae2b;
  color: white;
}

.calendar-cell:hover {
  background-color: #3dae2b;
  color: white;
}

.add-event-button {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  margin-top: 20px;
}

.add-event-button:hover {
  background-color: #218838;
}
</style>


