import { defineStore } from 'pinia';
import axiosInstance from '@/axios'; // axiosInstance import

export const useSessionStore = defineStore('session', {
  state: () => ({
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

    async startConference(meetingId, userId) {
      try {
        const response = await axiosInstance.post(`/api/sessions/${meetingId}/${userId}`, { customSessionId: String(meetingId) });

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
        // this.meetingId = sessionId;
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
          this.session = null;
          this.token = null;
          this.streams = [];
          await this.saveSTTFinishedMeeting(meetingId); // save STT: redis -> mysql
        } else {
          console.error('Failed to end session:', response.data.message);
        }
        return response.data.success;
      } catch (error) {
        console.error('Failed to end session:', error);
        throw error;
      }
    },
    async getTeamId(meetingId) {
      try {
        const response = await axiosInstance.get(`/api/meetings/${meetingId}`);
        return response.data.result.team_id;
      } catch (error) {
        console.error('Failed to get team ID:', error);
        throw error;
      }
    },
    async saveSTTFinishedMeeting(meetingId) {
      try {
        const response = await axiosInstance.get(`/api/meetings/${meetingId}/finish`);
        console.log("Meeting finished successfully");
        console.log("Response status:", response.status);
        console.log("Response data:", response.data);
      } catch (error) {
        console.error('Failed to finish meeting:', error.response ? error.response.data : error.message);
      }
    }
  },
});
