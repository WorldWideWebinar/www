<template>
  <div>
    <h1>Session Room</h1>
    <div id="video-container"></div>
    <button @click="joinSession">Join Session</button>
    <button @click="leaveSession">Leave Session</button>
  </div>
</template>

<script>
import { OpenVidu } from 'openvidu-browser';
import { useSessionStore } from '../stores/sessionStore';
import axios from 'axios';

export default {
  setup() {
    const sessionStore = useSessionStore();
    const OV = new OpenVidu();
    let session;

    const joinSession = async () => {
      session = OV.initSession();
      sessionStore.setSession(session);

      session.on('streamCreated', (event) => {
        const subscriber = session.subscribe(event.stream, 'video-container');
        sessionStore.addStream(subscriber);
      });

      session.on('streamDestroyed', (event) => {
        sessionStore.removeStream(event.stream.streamId);
      });

      try {
        // 세션 생성 요청
        const sessionResponse = await axios.post('http://localhost:5000/api/sessions', { customSessionId: "TestSession" });
        const sessionId = sessionResponse.data.id;
        console.log('Session ID:', sessionId);

        // 토큰 생성 요청
        const tokenResponse = await axios.post(`http://localhost:5000/api/sessions/${sessionId}/connection`);
        const token = tokenResponse.data.token;
        console.log('Token:', token);

        await session.connect(token, { clientData: 'Participant' });

        const publisher = OV.initPublisher('video-container');
        session.publish(publisher);
      } catch (error) {
        console.error('Error connecting to session:', error);
      }
    };

    const leaveSession = () => {
      if (session) {
        session.disconnect();
        sessionStore.setSession(null);
      }
    };

    return {
      joinSession,
      leaveSession
    };
  }
};
</script>

<style>
#video-container {
  display: flex;
  flex-direction: row;
}
</style>
