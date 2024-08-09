package com.ssafy.globalcc.domain.team.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.team.dto.TeamDetailDto;
import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.dto.TeamOutDto;
import com.ssafy.globalcc.domain.team.exception.MemberNotFoundException;
import com.ssafy.globalcc.domain.team.exception.NoSuchTeamException;
import com.ssafy.globalcc.domain.team.exception.TeamCreationFailException;
import com.ssafy.globalcc.domain.team.service.TeamService;
import com.ssafy.globalcc.domain.user.dto.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamDto teamDto) {
        int teamId = teamService.addTeam(teamDto);
        return new ResponseEntity<>(ApiResponse.success(teamId,"팀 생성 성공"), HttpStatus.OK);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable int teamId) {
        TeamDetailDto result = teamService.getTeamDetails(teamId);
        return new ResponseEntity<>(ApiResponse.success(result,"팀 정보 조회 성공"), HttpStatus.OK);
    }

    @PutMapping("{teamId}/{userId}")
    public ResponseEntity<?> updateTeam(@PathVariable int teamId, @PathVariable int userId) {
        TeamOutDto dto = TeamOutDto.builder().teamId(teamId).userId(userId).build();
        teamService.userOutTeam(dto);
        return new ResponseEntity<>(ApiResponse.success(dto,"유저 퇴장 성공"),HttpStatus.OK);
    }


    @DeleteMapping("{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable int teamId, @AuthenticationPrincipal SecurityUser user) {
        teamService.deleteTeam(teamId, user.getUsername());
        return new ResponseEntity<>(ApiResponse.success(teamId,"팀 삭제 성공"),HttpStatus.OK);
    }

    @PutMapping("{teamId}")
    public ResponseEntity<?> updateTeamInfo(@PathVariable int teamId, @RequestBody TeamDto teamDto, @AuthenticationPrincipal SecurityUser user) {
        teamService.updateTeam(teamId, teamDto, user.getUsername());
        return new ResponseEntity<>(ApiResponse.success(teamId, "팀 정보 수정 성공"), HttpStatus.OK);
    }

    @ExceptionHandler(TeamCreationFailException.class)
    public ResponseEntity<?> handleTeamCreationFail(TeamCreationFailException e) {
        log.error("Team creation failed", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("팀생성 실패");
    }
    @ExceptionHandler(NoSuchTeamException.class)
    public ResponseEntity<?> handleNoSuchTeam(NoSuchTeamException e) {
        log.error("Team not found", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("팀 정보 조회 실패");
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<?> handleMemberNotFound(MemberNotFoundException e) {
        log.error("Member not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("팀원 정보 조회 실패");
    }
}
