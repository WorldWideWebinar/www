package com.ssafy.globalcc.domain.meetingSTT.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSTTSegment {
    private double start;
    private double end;
    private String text;
}
