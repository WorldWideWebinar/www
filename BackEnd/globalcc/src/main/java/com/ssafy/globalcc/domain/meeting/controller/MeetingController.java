package com.ssafy.globalcc.domain.meeting.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.meeting.dto.MeetingDetailsDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingListDto;
import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.exception.NoSuchMeetingException;
import com.ssafy.globalcc.domain.meeting.service.MeetingService;
import com.ssafy.globalcc.domain.team.exception.NoSuchTeamException;
import com.ssafy.globalcc.domain.team.exception.NotTeamOwnerException;
import com.ssafy.globalcc.domain.user.dto.SecurityUser;
import com.ssafy.globalcc.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("api/meetings")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody MeetingDto meeting, @AuthenticationPrincipal SecurityUser user) {
        int meeting_id = meetingService.addMeeting(meeting, user.getUsername());
        return new ResponseEntity<>(ApiResponse.success(meeting_id,"미팅 생성 성공"), HttpStatus.OK);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<?> getMeeting(@PathVariable int meetingId) {
        MeetingDetailsDto result = meetingService.findMeetingDetailById(meetingId);
        return new ResponseEntity<>(ApiResponse.success(result,"미팅 조회 성공"), HttpStatus.OK);
    }
    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeeting(@PathVariable int meetingId,
                                           @RequestBody MeetingDto meetingDto, @AuthenticationPrincipal SecurityUser user) {
        MeetingDetailsDto ressult = meetingService.updateMeetingDetails(meetingId,meetingDto,user.getUsername());
        return new ResponseEntity<>(ApiResponse.success(ressult,"미팅 수정 성공"), HttpStatus.OK);

    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<?> deleteMeeting(@PathVariable int meetingId, @AuthenticationPrincipal SecurityUser user) {
        meetingService.delete(meetingId);
        return new ResponseEntity<>(ApiResponse.success(meetingId,"미팅 삭제 완료"),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllMeetings(@RequestParam int teamId,
                                            @RequestParam ZonedDateTime today,
                                            @RequestParam boolean prev,
                                            @RequestParam boolean next) {
        MeetingListDto dto = new MeetingListDto(teamId,today,prev,next);
        List<MeetingDetailsDto> list = meetingService.findMEetingListByDto(dto);
        return new ResponseEntity<>(ApiResponse.success(list,"미팅 리스트 조회 성공!"),HttpStatus.OK);
    }



    @ExceptionHandler(NotTeamOwnerException.class)
    public ResponseEntity<?> notTeamOwner(NotTeamOwnerException e) {
        log.debug("Not team owner exception : ", e);
        return new ResponseEntity<>(ApiResponse.fail("","미팅 생성 권한 없음."),HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(NoSuchTeamException.class)
    public ResponseEntity<?> noTeamForId(NoSuchTeamException e) {
        log.debug("No team exception", e);
        return new ResponseEntity<>(ApiResponse.fail("","팀 존재하지 않음."),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NoSuchMeetingException.class)
    public ResponseEntity<?> noMeetingForId(NoSuchMeetingException e) {
        return new ResponseEntity<>(ApiResponse.fail("","미팅 존재하지 않음."),HttpStatus.NOT_FOUND);
    }
}
