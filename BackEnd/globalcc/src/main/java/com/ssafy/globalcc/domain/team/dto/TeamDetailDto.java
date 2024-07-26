package com.ssafy.globalcc.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TeamDetailDto {
    private int ownerId;
    private String teamName;
    private List<Integer> userList;
    private List<Integer> meetingList;
    private String emoji;
    private LocalDateTime createdAt;
}
