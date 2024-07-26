package com.ssafy.globalcc.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class TeamDto {
    private Integer ownerId;
    private String teamName;
    private List<String> userList;

}
