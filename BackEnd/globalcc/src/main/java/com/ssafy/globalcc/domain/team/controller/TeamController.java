package com.ssafy.globalcc.domain.team.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.exception.TeamCreationFailException;
import com.ssafy.globalcc.domain.team.result.TeamDetailResult;
import com.ssafy.globalcc.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
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
        TeamDetailResult result = teamService.getTeamDetails(teamId);
        return new ResponseEntity<>(ApiResponse.success(result,"팀 정보 조회 성공"), HttpStatus.OK);
    }

    @ExceptionHandler(TeamCreationFailException.class)
    public ResponseEntity<?> handleTeamCreationFail(TeamCreationFailException e) {
        log.error("Team creation failed", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("팀생성 실패");
    }
}
