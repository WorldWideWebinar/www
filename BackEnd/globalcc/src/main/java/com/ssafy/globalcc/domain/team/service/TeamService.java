package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.result.TeamDetailResult;

public interface TeamService {
    int addTeam(TeamDto dto);

    TeamDetailResult getTeamDetails(int teamId);
}
