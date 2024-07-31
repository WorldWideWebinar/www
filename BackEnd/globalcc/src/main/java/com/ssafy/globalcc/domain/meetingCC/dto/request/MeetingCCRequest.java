package com.ssafy.globalcc.domain.meetingCC.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingCCRequest {
    private String countryCode;
    // 음성 스트림 추가
}
