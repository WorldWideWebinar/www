package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.dto.TeamOutDto;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.exception.MemberNotFoundException;
import com.ssafy.globalcc.domain.team.exception.NoSuchTeamException;
import com.ssafy.globalcc.domain.team.exception.NotTeamOwnerException;
import com.ssafy.globalcc.domain.team.repository.TeamRepository;
import com.ssafy.globalcc.domain.team.dto.TeamDetailDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.entity.UserTeam;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final MeetingRepository meetingRepository;

    @Override
    @Transactional
    public int addTeam(TeamDto dto) {
//        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow();
        log.debug("creating new Team with : {}",dto);
        User owner = User.builder().userId(dto.getOwnerId()).build();
        // uid를 기준으로 유저 목록을 불러옴.

        List<User> userList = userRepository.findUsersByUidIn(dto.getUserList());
        Team team = new Team();
        team.setName(dto.getTeamName());
        team.setOwner(owner);
        team = teamRepository.save(team);
        log.debug("saved team: {}", team);
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
    public TeamDetailDto getTeamDetails(int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoSuchTeamException::new);
        return TeamDetailDto.builder()
                .teamName(team.getName())
                .emoji(team.getEmoji())
                .ownerId(team.getOwner().getUserId())
                .meetingList(team.getMeeting().stream()
                        .map((Meeting::getMeetingId))
                        .toList())
                .userList(team.getUserTeam().stream()
                        .map(userTeam -> userTeam.getUser().getUserId())
                        .toList())
                .createdAt(team.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void userOutTeam(TeamOutDto dto) {
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(NoSuchTeamException::new);
        boolean isOwner = team.getOwner().getUserId().equals(dto.getUserId());
        if(isOwner){
            log.debug("Owner leaving {}",dto);
            List<UserTeam> userTeams = team.getUserTeam();
            if(userTeams.size() == 1){
                // 팀장이 유일한 팀원인가? -> 팀을 삭제
                teamRepository.delete(team);
                return;
            } else{
                // 팀장외에 팀원이 남아있다면 -> 팀원 중 누군가에게 팀장의 권한이 넘어감.
                int newOwnerId = userTeams.get(0).getUser().getUserId() == dto.getUserId() ?
                        userTeams.get(1).getUser().getUserId() : userTeams.get(0).getUser().getUserId();
                log.debug("new Owner of team : {}",newOwnerId);
                team.setOwner(User.builder().userId(newOwnerId).build());
                teamRepository.save(team); //팀장 변경
            }
        }
        int row = userTeamRepository.deleteByUserUserId(dto.getUserId(), dto.getTeamId());
        if(row == 0) throw new MemberNotFoundException();
    }

    @Override
    @Transactional
    public void deleteTeam(int teamId, String userUid) {
        int row = teamRepository.deleteTeamByTeamIdAndOwnerUid(teamId,userUid);
        if(row == 0) throw new NotTeamOwnerException();
    }

}
