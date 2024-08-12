package com.ssafy.globalcc.domain.session.controller;

import com.ssafy.globalcc.domain.meeting.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebHookController {

    private final MeetingService meetingService;

    public WebHookController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping()
    public ResponseEntity<String> handleSessionDestroyed(@RequestBody Map<String, Object> payload) {
        // OpenVidu에서 전송한 sessionId 추출
        String sessionId = (String) payload.get("sessionId");

        // sessionId로 meetingId 조회
        String meetingId = meetingService.findMeetingIdBySessionId(sessionId);

        if (meetingId != null) {
            // meetingId로 리디렉션
            String redirectUrl = "/meetingSTT/" + meetingId;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .body("Redirecting to " + redirectUrl);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Meeting ID found for sessionId: " + sessionId);
        }
    }
}


