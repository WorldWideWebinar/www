package com.ssafy.globalcc.domain.meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class MeetingDto {
    private int team_id;
    private String name;
    private String detail;

    private LocalDateTime start;
    private LocalDateTime end;

}
