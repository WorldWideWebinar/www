package com.ssafy.globalcc.domain.meetingSTT.dto.response;

import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTSegment;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingSTTResponse {

    private Integer meetingId;
    private String content;
    private String timestamp;
    private List<MeetingSTTSegment> segments;

    public static MeetingSTTResponse of(Integer meetingId, String content, List<MeetingSTTSegment> segments) {
        return MeetingSTTResponse.builder()
                .meetingId(meetingId)
                .content(content)
                .segments(segments)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}
