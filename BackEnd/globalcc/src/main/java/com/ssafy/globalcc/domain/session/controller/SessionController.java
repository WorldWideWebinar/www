package com.ssafy.globalcc.domain.session.controller;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.*;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequestMapping("/api/sessions")
public class SessionController {

    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    private final MeetingService meetingService;

    public SessionController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }


    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     * 세션 초기화 /api/sessions/{meetingId}/{userId}
     *
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/{meetingId}/{userId}")
    public ResponseEntity<String> initializeSession(@PathVariable("meetingId") Integer meetingId,
                                                    @PathVariable("userId") Integer userId,
                                                    @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Meeting meeting = meetingService.findMeetingById(meetingId);

        // 이미 sessionId가 있는지 확인
        if (meeting.getSessionId() != null && !meeting.getSessionId().isEmpty()) {
            return new ResponseEntity<>("Session already exists with ID: " + meeting.getSessionId(), HttpStatus.BAD_REQUEST);
        }

        // 팀장인지 확인
        if (!meetingService.isUserTeamLeader(meetingId, userId)) {
            return new ResponseEntity<>("Only the team leader can create a session", HttpStatus.FORBIDDEN);
        }

        // 세션 생성
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties);
        String sessionId = session.getSessionId();

        // meeting의 session_id 업데이트
        meetingService.updateMeetingSessionId(meetingId, sessionId);

        return new ResponseEntity<>(sessionId, HttpStatus.OK);
    }

    /**
     * 세션에서 새로운 연결 만듦 /api/sessions/{sessionId}/connection
     *
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

}

