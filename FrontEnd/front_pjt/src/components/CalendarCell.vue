<template>
  <div class="calendar-cell"
       :class="{ 'hasMeeting': hasMeeting, 'currentMonth': day.currentMonth, 'selected': selected }"
       @click="$emit('select', day)">
    {{ day.date.format('D') }}
    <span v-if="hasMeeting" class="meeting-marker"></span>
  </div>
</template>

<script setup>
const props = defineProps({
  day: Object,
  hasMeeting: Boolean,
  selected: Boolean
});

const emit = defineEmits(['select']);
</script>

<style scoped>
.calendar-cell {
  position: relative;
  padding: 10px;
  text-align: center;
  border-radius: 10px;
  cursor: pointer;
  background-color: #f3e5f5; /* 기본 날짜의 약한 색상 */
  transition: background-color 0.3s;
}

.calendar-cell.hasMeeting .meeting-marker {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 8px;
  height: 8px;
  background-color: #F8EFBB; /* 보색 1 */
  border-radius: 50%;
}

.calendar-cell.currentMonth {
  background-color: #f8bbd0; /* 보색 1 */
}

.calendar-cell:not(.currentMonth) {
  background-color: #f8eafc; /* 기본 날짜의 약한 색상 */
}

.calendar-cell.selected {
  background-color: #F8EFBB; /* 보색 1 */
  color: #1d252c;
}

.calendar-cell:hover {
  background-color: #f3e5f5; /* hover 색상 및 daysOfWeek 색상 */
  color: #1d252c;
}
</style>
