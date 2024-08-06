package com.ssafy.globalcc.domain.chat.dto.response;

import com.ssafy.globalcc.domain.meetingSTT.dto.response.MeetingSTTResponse;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private int senderId;
    private int teamId;
    private String content;
    private String contentType;
    private String createdAt;

    public static ChatResponse of(Integer senderId, Integer teamId, String content) {
        return ChatResponse.builder()
                .senderId(senderId)
                .teamId(teamId)
                .content(content)
                .contentType("text")
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}
