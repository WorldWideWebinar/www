import express from 'express';
import axios from 'axios';
import cors from 'cors';
import bodyParser from 'body-parser';

const app = express();
const PORT = 5000;

app.use(cors());
app.use(bodyParser.json({ limit: '10mb' }));

const OPENVIDU_URL = 'http://localhost:4443';
const OPENVIDU_SECRET = '1234'; // 여기에 Docker 명령어에서 사용한 키와 동일한 값을 설정

app.post('/api/sessions', async (req, res) => {
  const { customSessionId } = req.body; // 커스텀 세션 ID를 요청 본문에서 가져옴
  try {
    const response = await axios.post(`${OPENVIDU_URL}/openvidu/api/sessions`, { customSessionId }, {
      auth: {
        username: 'OPENVIDUAPP',
        password: OPENVIDU_SECRET
      }
    });
    console.log('Session created:', response.data);
    res.json(response.data);
  } catch (error) {
    console.error('Error creating session:', error.response ? error.response.data : error.message);
    res.status(500).send(error.message);
  }
});

app.post('/api/sessions/:sessionId/connection', async (req, res) => {
  const { sessionId } = req.params;
  try {
    const response = await axios.post(`${OPENVIDU_URL}/openvidu/api/sessions/${sessionId}/connection`, {}, {
      auth: {
        username: 'OPENVIDUAPP',
        password: OPENVIDU_SECRET
      }
    });
    console.log('Token created:', response.data);
    res.json(response.data);
  } catch (error) {
    console.error('Error creating token:', error.response ? error.response.data : error.message);
    res.status(500).send(error.message);
  }
});

app.post('/api/audio', (req, res) => {
  const audioData = req.body.audioData;
  // 여기서 audioData를 처리하여 자막을 생성합니다.
  res.json({ message: 'Audio data received successfully' });
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
