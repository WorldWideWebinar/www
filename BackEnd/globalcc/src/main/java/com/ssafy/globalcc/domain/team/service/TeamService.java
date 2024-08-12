package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.dto.TeamDetailDto;
import com.ssafy.globalcc.domain.team.dto.TeamOutDto;
import com.ssafy.globalcc.domain.team.entity.Team;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public interface TeamService {
    int addTeam(TeamDto dto);

    TeamDetailDto getTeamDetails(int teamId);

    void userOutTeam(TeamOutDto dto);

    void deleteTeam(int teamId, String username);

    Team findTeamById(int teamId);

    Team getTeamByIdAndOwnerUid(int teamId, String username);

    void updateTeam(int teamId, TeamDto teamDto, String ownerUid);

    ResponseEntity<?> getEmojis();
}
