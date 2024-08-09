package com.ssafy.globalcc.domain.meetingSTT.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSTTSegment {
    private float start;
    private float end;
    private String text;
}
