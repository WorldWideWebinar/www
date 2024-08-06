package com.ssafy.globalcc.domain.meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Setter
public class MeetingDto {
    private int team_id;
    private String name;
    private String detail;
    private ZonedDateTime start;
    private ZonedDateTime  end;

}
