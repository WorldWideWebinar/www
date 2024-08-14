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
    meetingInfo: null,
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

        this.inConference = true;
        return this.sessionId; // sessionId 반환
      } catch (error) {
        this.inConference = true;
        console.error('Failed to create OpenVidu session:', error);
        throw error;
      }
    },
    async joinConference(sessionId) {
      try {
        const response = await axiosInstance.post(`/api/sessions/${sessionId}/connections`);
        this.sessionId = sessionId;
        this.token = response.data; // 서버로부터 받은 토큰을 저장
        this.sessionId = sessionId;
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
    
    async fetchMeetingById(meetingId) {
      try {
        const response = await axiosInstance.get(`/api/meetings/${meetingId}`);
        this.meetingInfo = response.data.result;  // 미팅 정보 저장
        return this.meetingInfo;
      } catch (error) {
        console.error('Failed to fetch meeting:', error);
        return null;
      }
    },

    async saveSTTFinishedMeeting(meetingId) {
      try {
        await axiosInstance.get(`/api/meetings/${meetingId}/finish`);
      } catch (error) {
        console.error('Failed to finish meeting:', error.response ? error.response.data : error.message);
      }
    },
  },
});
