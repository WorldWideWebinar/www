package com.ssafy.globalcc.domain.meetingCC.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.globalcc.domain.meetingCC.dto.request.MeetingCCRequest;
import com.ssafy.globalcc.domain.meetingCC.dto.response.MeetingCCResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class MeetingCCHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        MeetingCCRequest request = objectMapper.readValue(payload, MeetingCCRequest.class);
        String translatedText = "translateTestMessage"; // 이후 AI 서버 연동하여 실제 번역 텍스트로 변경

        MeetingCCResponse response = MeetingCCResponse.of(request.getCountryCode(), translatedText);
        String responseJson = objectMapper.writeValueAsString(response);
        session.sendMessage(new TextMessage(responseJson));
    }
}