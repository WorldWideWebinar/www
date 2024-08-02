import { defineStore } from 'pinia';
import axiosInstance from '@/axios'; // axiosInstance import
import { useRouter } from 'vue-router';

export const useSessionStore = defineStore('session', {
  state: () => ({
    session: null,
    sessionId: null,
    streams: [],
    inConference: false
  }),
  actions: {
    setSession(session) {
      this.session = session;
    },
    addStream(stream) {
      this.streams.push(stream);
    },
    removeStream(streamId) {
      this.streams = this.streams.filter(stream => stream.id !== streamId);
    },
    async startConference(meetingId, userId, customSessionId = 'TestSession') {
      const router = useRouter();
      try {
        const response = await axiosInstance.post(`/api/sessions/${meetingId}/${userId}`, { customSessionId });

        this.sessionId = response.data.id;
        console.log('Starting conference with OpenVidu, sessionId:', this.sessionId);

        router
          .push({ name: 'ConferenceView', params: { sessionId: this.sessionId } })
          .then(() => {
            this.inConference = true;
          })
          .catch((err) => {
            console.error('Error navigating to ConferenceView:', err);
          });
      } catch (error) {
        console.error('Failed to create OpenVidu session:', error);
      }
    }
  }
});

