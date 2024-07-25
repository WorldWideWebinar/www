package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.repository.TeamRepository;
import com.ssafy.globalcc.domain.team.result.TeamDetailResult;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.entity.UserTeam;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;

    @Override
    @Transactional
    public int addTeam(TeamDto dto) {
//        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow();
        User owner = User.builder().userId(dto.getOwnerId()).build();
        // uid를 기준으로 유저 목록을 불러옴.

        List<User> userList = userRepository.findUsersByUidIn(dto.getTeamMembers());
        Team team = new Team();
        team.setName(dto.getName());
        team.setOwner(owner);
        team = teamRepository.save(team);
        Team finalTeam = team;
        List<UserTeam> userTeams = new ArrayList<>(userList.stream().map((user) ->
                UserTeam.builder()
                        .team(finalTeam)
                        .user(user)
                        .admission(true)
                        .lastTime(null)
                        .build()
        ).toList());
        userTeams.add(UserTeam.builder()
                .team(finalTeam)
                .user(owner)
                .admission(true)
                .lastTime(null)
                .build());
        userTeamRepository.saveAll(userTeams);
        return finalTeam.getTeamId();
    }

    @Override
    public TeamDetailResult getTeamDetails(int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        // TODO : meeting list 불러오기. by Team id
        List<Integer> userList = userTeamRepository.findUserIdByTeam(team);
        return TeamDetailResult.builder()
                .teamName(team.getName())
                .emoji(team.getEmoji())
                .ownerId(team.getOwner().getUserId())
                .userList(userList)
                .createdAt(team.getCreatedAt())
                .build();
    }
}
