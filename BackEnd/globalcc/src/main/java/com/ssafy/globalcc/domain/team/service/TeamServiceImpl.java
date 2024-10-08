package com.ssafy.globalcc.domain.team.service;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.exception.NoSuchMeetingException;
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
import com.ssafy.globalcc.domain.user.repository.UserMeetingRepository;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

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
    private final UserMeetingRepository userMeetingRepository;
    @Value("${emoji.base}")
    private String baseEmojiURL;
    @Value("${emoji.api.key}")
    private String accessKey;
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
        team.setEmoji(dto.getEmoji());
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
        Team team = teamRepository.findById(teamId).orElseThrow(NoSuchTeamException::new);
        List<Meeting> meetingList =  team.getMeeting();
        if(meetingList != null && !meetingList.isEmpty()){
            meetingRepository.deleteAll(meetingList);
        }
        teamRepository.delete(team);
    }

    @Override
    public Team findTeamById(int teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchMeetingException("Team with ID " + teamId + " not found."));
    }

    @Override
    public Team getTeamByIdAndOwnerUid(int teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoSuchTeamException::new);
        if(!username.equals(team.getOwner().getUid())) throw new NotTeamOwnerException();
        return team;
    }

    @Override
    @Transactional
    public void updateTeam(int teamId, TeamDto teamDto, String ownerUid) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoSuchTeamException::new);

        if (!team.getOwner().getUid().equals(ownerUid)) {
            throw new NotTeamOwnerException();
        }

        team.setName(teamDto.getTeamName());
        team.setEmoji(teamDto.getEmoji());

        // 새로운 사용자 목록을 기반으로 UserTeam 업데이트
        List<User> existingUsers = team.getUserTeam().stream()
                .map(UserTeam::getUser)
                .toList();

        List<User> newUsers = userRepository.findUsersByUidIn(teamDto.getUserList());

        // 새로운 유저 추가
        List<UserTeam> newUserTeams = new ArrayList<>();
        for (User newUser : newUsers) {
            if (!existingUsers.contains(newUser)) {
                newUserTeams.add(UserTeam.builder()
                        .team(team)
                        .user(newUser)
                        .admission(true)
                        .lastTime(null)
                        .build());
            }
        }

        // 기존 UserTeam과 비교해서 삭제된 유저 처리
        for (User existingUser : existingUsers) {
            if (!newUsers.contains(existingUser)) {
                userTeamRepository.deleteByUserUserId(existingUser.getUserId(), teamId);
            }
        }
        userTeamRepository.saveAll(newUserTeams);

        teamRepository.save(team);
    }

    @Override
    public ResponseEntity<?> getEmojis() {
        RestClient client = RestClient.builder()
                .baseUrl("https://emoji-api.com/emojis?access_key=" + accessKey)
                .build();
        return client.get().retrieve().toEntity(String.class);
    }

}
