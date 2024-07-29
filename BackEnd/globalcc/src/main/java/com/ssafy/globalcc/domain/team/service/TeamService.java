package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.dto.TeamDetailDto;
import com.ssafy.globalcc.domain.team.dto.TeamOutDto;

public interface TeamService {
    int addTeam(TeamDto dto);

    TeamDetailDto getTeamDetails(int teamId);

    void userOutTeam(TeamOutDto dto);

    void deleteTeam(int teamId, String username);
}
