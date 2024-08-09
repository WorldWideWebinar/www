package com.ssafy.globalcc.domain.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MeetingListDto {
    int teamId;
    ZonedDateTime today;
    boolean prev;
    boolean next;
}
