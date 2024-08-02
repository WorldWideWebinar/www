package com.ssafy.globalcc.domain.session.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.service.MeetingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
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

    // TO-DO: config 설정

    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    private final MeetingServiceImpl meetingServiceImpl;

    public SessionController(MeetingServiceImpl meetingServiceImpl) {
        this.meetingServiceImpl = meetingServiceImpl;
    }

    @PostConstruct
    public void init() {
        if (OPENVIDU_URL == null || OPENVIDU_URL.isEmpty()) {
            throw new IllegalArgumentException("OPENVIDU_URL is not specified.");
        }
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     * 세션 초기화 /api/sessions/{meetingId}/{userId}
     *
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/{meetingId}/{userId}")
    public ResponseEntity<?> initializeSession(@PathVariable("meetingId") Integer meetingId,
                                               @PathVariable("userId") Integer userId,
                                               @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Meeting meeting = meetingServiceImpl.findMeetingById(meetingId);

        // 이미 sessionId가 있는지 확인
        if (meeting.getSessionId() != null && !meeting.getSessionId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail("", "세션이 이미 있습니다"));
        }

        // 팀장인지 확인
        if (!meetingServiceImpl.isUserTeamLeader(meetingId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail("", "팀장만 세션을 만들 수 있습니다"));
        }

        // 세션 생성
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties);
        String sessionId = session.getSessionId();

        // meeting의 session_id 업데이트
        meetingServiceImpl.updateMeetingSessionId(meetingId, sessionId);

        return ResponseEntity.ok(ApiResponse.success(sessionId, "세션 만들기 성공"));
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

    /**
     * 세션의 모든 연결 강제 종료 /api/sessions/{meetingId}
     *
     * @return The Meeting ID
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Integer>> deleteSession(@PathVariable("meetingId") Integer meetingId)
            throws OpenViduJavaClientException, OpenViduHttpException {

        // 서버 상태 업데이트
        openvidu.fetch();

        Meeting meeting = meetingServiceImpl.findMeetingById(meetingId);
        if (meeting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(null, "미팅 정보 찾기 실패"));
        }

        // 활성화된 해당 세션 검색
        String sessionId = meeting.getSessionId();
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(null, "세션 찾기 실패"));
        }

        // 모든 활성화된 연결 정보를 가져옴
        List<Connection> connections = session.getActiveConnections();

        boolean allDisconnected = true;
        for (Connection connection : connections) {
            try {
                // 사용자 연결 끊기
                session.forceDisconnect(connection);
            } catch (Exception e) {
                allDisconnected = false; // 연결 삭제 비정상
            }
        }

        if (!allDisconnected) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "커넥션 끊기 실패"));
        }

        // 모든 연결을 삭제했다면 meeting에서 sessionId 값 제거
        meetingServiceImpl.updateMeetingSessionId(meetingId, null);

        return ResponseEntity.ok(ApiResponse.success(meetingId, "커넥션 끊기 성공"));
    }

    /**
     * Openvidu URL 확인 /api/sessions/check
     *
     * @return ResponseEntity
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkOpenviduUrl() {
        log.debug("Checking OpenVidu URL accessibility... : {}", OPENVIDU_URL);
        try {
            this.openvidu.fetch();
            log.debug("OpenVidu URL is accessible.");
            return new ResponseEntity<>("URL is accessible", HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("Failed to access OpenVidu URL: {}", e.getMessage(), e);
            return new ResponseEntity<>("URL is not accessible: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

