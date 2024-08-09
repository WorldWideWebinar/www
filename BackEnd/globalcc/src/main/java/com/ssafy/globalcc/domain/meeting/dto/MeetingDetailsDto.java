package com.ssafy.globalcc.domain.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Builder
public class MeetingDetailsDto {
    private Integer meeting_id;
    private Integer team_id;
    private String name;
    private ZonedDateTime start_at;
    private ZonedDateTime end_at;
    private String details;
    private String content;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String session_id;

}
