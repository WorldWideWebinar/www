package com.ssafy.globalcc.domain.meetingSTT.dto.response;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class MeetingSTTResponse {
    private String meetingId;
    private String content;
    private String timestamp;

    public static MeetingSTTResponse of(String meetingId, String content) {
        return MeetingSTTResponse.builder()
                .meetingId(meetingId)
                .content(content)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}
