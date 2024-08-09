package com.ssafy.globalcc.domain.meetingSTT.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingSTTRequest {
    private Integer meetingId;
    private String content;
    private List<MeetingSTTSegment> segments;
}
