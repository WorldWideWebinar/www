package com.ssafy.globalcc.domain.meetingSTT.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSTTRequest {
    private String meetingId;
    private String content;
}
