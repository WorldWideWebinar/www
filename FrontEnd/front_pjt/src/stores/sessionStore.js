import { defineStore } from 'pinia';
import axiosInstance from '@/axios'; // axiosInstance import

export const useSessionStore = defineStore('session', {
  state: () => ({
    isHost: true,
    session: null,
    sessionId: null,
    streams: [],
    inConference: false,
    token: null, // 토큰을 저장할 변수 추가
    meetingId: null,
    participants: 0,
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
    async startConference(meetingId, userId, customSessionId) {
      try {
        const response = await axiosInstance.post(`/api/sessions/${meetingId}/${userId}`, { customSessionId });

        this.sessionId = response.data.result;
        this.meetingId = meetingId;
        console.log('Starting conference with OpenVidu, sessionId:', this.sessionId);

        this.inConference = true;
        return this.sessionId; // sessionId 반환
      } catch (error) {
        this.inConference = true;
        console.error('Failed to create OpenVidu session:', error);
        throw error;
      }
    },
    async joinConference(sessionId) {
      console.log('참가하려는 세션', sessionId)
      try {
        const response = await axiosInstance.post(`/api/sessions/${sessionId}/connections`);

        this.token = response.data; // 서버로부터 받은 토큰을 저장
        console.log('openvidu 발급 토큰', this.token);

        this.inConference = true;
        return this.token; // 토큰 반환
      } catch (error) {
        console.error('Failed to join conference:', error);
        throw error;
      }
    },
    async endSession(meetingId) {
      try {
        const response = await axiosInstance.delete(`/api/sessions/${meetingId}`);
        if (response.data.success) {
          console.log('Session ended successfully');
          this.sessionId = null;
          this.inConference = false;
          this.token = null;
          this.streams = [];
        } else {
          console.error('Failed to end session:', response.data.message);
        }
        return response.data.success;
      } catch (error) {
        console.error('Failed to end session:', error);
        throw error;
      }
    },
  }
});