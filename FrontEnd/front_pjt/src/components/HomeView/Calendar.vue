<template>
  <div class="cal-container">
    <div class="cal-content">
      <h3>Calendar</h3>
      <div id="calendar">
        <div class="calendar-header">
          <button @click="previousMonth" class="nav-button">&lt;</button>
          <h2 style="font-size: 28px;">{{ currentMonth.format('MMMM YYYY') }}</h2>
          <button @click="nextMonth" class="nav-button">&gt;</button>
        </div>
        <div class="calendar">
          <div class="calendar-row calendar-header-row">
            <div class="calendar-cell" v-for="day in daysOfWeek" :key="day">{{ day }}</div>
          </div>
          <div class="calendar-row" v-for="week in calendarDays" :key="week[0].date">
            <CalendarCell
              v-for="day in week"
              :key="day.date"
              :day="day"
              :has-meeting="hasMeeting(day.date)"
              :selected="isSelected(day)"
              @select="selectDay(day)"
            />
          </div>
        </div>
      </div>
      <CalendarCard v-if="selectedMeetings.length" :meetings="selectedMeetings"/>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import CalendarCard from './CalendarCard.vue'
import CalendarCell from './CalendarCell.vue'
import dayjs from 'dayjs'

const userStore = useUserStore()
const currentMonth = ref(dayjs().startOf('month'))
const selectedDate = ref(null)

const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

const calendarDays = computed(() => {
  const startOfMonth = currentMonth.value.startOf('month')
  const endOfMonth = currentMonth.value.endOf('month')
  const startDate = startOfMonth.startOf('week')
  const endDate = endOfMonth.endOf('week')

  const days = []
  let date = startDate

  while (date.isBefore(endDate, 'day')) {
    const week = []
    for (let i = 0; i < 7; i++) {
      week.push({
        date: date.clone(),
        currentMonth: date.isSame(currentMonth.value, 'month')
      })
      date = date.add(1, 'day')
    }
    days.push(week)
  }
  return days
})

const selectedMeetings = computed(() => {
  if (!selectedDate.value || !userStore.meetings) return []
  return userStore.meetings.filter(
    (meeting) =>
      dayjs(meeting.start_at).format('YYYY-MM-DD') === selectedDate.value.format('YYYY-MM-DD')
  )
})

function selectDay(day) {
  selectedDate.value = day.date
}

function isSelected(day) {
  return selectedDate.value && selectedDate.value.isSame(day.date, 'day')
}

function hasMeeting(date) {
  return (
    userStore.meetings &&
    userStore.meetings.some(
      (meeting) => dayjs(meeting.start_at).format('YYYY-MM-DD') === date.format('YYYY-MM-DD')
    )
  )
}

function previousMonth() {
  currentMonth.value = currentMonth.value.subtract(1, 'month')
}

function nextMonth() {
  currentMonth.value = currentMonth.value.add(1, 'month')
}

onMounted(() => {
  // userStore.fetchUserMeetings();
});
</script>
<style scoped>
.cal-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  max-height: 100vh;
  background-color: #f5f5f5; /* 연한 회색 배경색 추가 */
}

.cal-content {
  background: #ffffff;
  color: #1d252c;
  padding: 40px 60px;
  text-align: center;
  margin: 0px auto;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.cal-content h3 {
  font-size: 45px;
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
  background-color: #bbe8f8; /* 보색 2 */
  color: #1d252c;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
}

.nav-button:hover {
  background-color: #a0d1e0; /* 보색 2 음영 */
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
  position: relative;
  padding: 10px;
  text-align: center;
  border-radius: 10px;
  cursor: pointer;
  background-color: #f3e5f5; /* 기본 날짜의 약한 색상 */
  transition: background-color 0.3s;
  width: 58px;
}

.calendar-header-row .calendar-cell {
  font-weight: bold;
  background-color: #f3e5f5; /* 메인 색상 */
}

.calendar-cell.currentMonth {
  background-color: #f8bbd0; /* 보색 1 */
}

.calendar-cell:not(.currentMonth) {
  background-color: #f8eafc; /* 기본 날짜의 약한 색상 */
}

.calendar-cell.hasMeeting .meeting-marker {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 8px;
  height: 8px;
  background-color: #f8efbb; /* 보색 1 */
  border-radius: 50%;
}

.calendar-cell.selected {
  background-color: #f8efbb; /* 보색 1 */
  color: #1d252c;
}

.calendar-cell:hover {
  background-color: #f3e5f5; /* hover 색상 및 daysOfWeek 색상 */
  color: #1d252c;
}
</style>
