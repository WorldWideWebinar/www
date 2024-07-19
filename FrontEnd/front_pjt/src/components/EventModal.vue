<template>
  <div class="modal-overlay" v-if="isOpen">
    <div class="modal">
      <h2>Create Event</h2>
      <form @submit.prevent="createEvent">
        <div>
          <label for="title">Title</label>
          <input type="text" v-model="title" required />
        </div>
        <div>
          <label for="date">Date</label>
          <input type="date" v-model="date" required />
        </div>
        <div>
          <label for="host">Host</label>
          <input type="text" v-model="host" required />
        </div>
        <div>
          <label for="participants">Participants</label>
          <input type="number" v-model="participants" required />
        </div>
        <button type="submit">Create</button>
        <button type="button" @click="closeModal">Cancel</button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  props: {
    isOpen: {
      type: Boolean,
      required: true
    }
  },
  emits: ['close', 'eventCreated'],
  setup(props, { emit }) {
    const title = ref('');
    const date = ref('');
    const host = ref('');
    const participants = ref(1);

    function closeModal() {
      emit('close');
    }

    function createEvent() {
      const newEvent = {
        title: title.value,
        date: date.value,
        host: host.value,
        participants: Array.from({ length: participants.value }, (_, i) => `Participant ${i + 1}`)
      };
      emit('eventCreated', newEvent);
      closeModal();
    }

    return {
      title,
      date,
      host,
      participants,
      closeModal,
      createEvent
    };
  }
};
</script>

<style>
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

.modal {
  background: white;
  padding: 1em;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
</style>
