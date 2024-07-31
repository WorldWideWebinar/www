package com.ssafy.globalcc.domain.meetingSTT.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingSTTResponse {

    private Integer meetingId;
    private String content;
    private String timestamp;

    public static MeetingSTTResponse of(Integer meetingId, String content) {
        return MeetingSTTResponse.builder()
                .meetingId(meetingId)
                .content(content)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}
