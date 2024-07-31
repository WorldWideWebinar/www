package com.ssafy.globalcc.domain.meetingCC.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingCCResponse {
    private String countryCode;
    private String content;
    private String timestamp;

    public static MeetingCCResponse of(String countryCode, String content) {
        return MeetingCCResponse.builder()
                .countryCode(countryCode)
                .content(content)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}