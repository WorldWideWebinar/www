package com.ssafy.globalcc.domain.team.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeamDto {
    private Integer ownerId;
    private String teamName;
    private List<String> userList;

}
