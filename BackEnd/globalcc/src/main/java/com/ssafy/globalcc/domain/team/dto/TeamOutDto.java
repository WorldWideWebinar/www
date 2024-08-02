package com.ssafy.globalcc.domain.team.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class TeamOutDto {
    private int teamId;
    private int userId;
}
