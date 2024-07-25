package com.ssafy.globalcc.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
public class TeamDto {
    private Integer ownerId;
    private String name;
    private List<String> teamMembers;

}
