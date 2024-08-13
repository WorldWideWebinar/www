package com.ssafy.globalcc.domain.meeting.service;

import com.ssafy.globalcc.domain.meeting.dto.MeetingDetailsDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingListDto;
import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.exception.NoSuchMeetingException;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.exception.NotTeamOwnerException;
import com.ssafy.globalcc.domain.team.service.TeamService;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.entity.UserMeeting;
import com.ssafy.globalcc.domain.user.repository.UserMeetingRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl implements MeetingService{

    private final MeetingRepository meetingRepository;
    private final TeamService teamService;
    private final UserMeetingRepository userMeetingRepository;
    private final UserTeamRepository userTeamRepository;

    /**
     * meetingId로 미팅을 조회하는 공통 메서드
     * @param meetingId The meeting ID
     * @return The Meeting entity
     * @throws IllegalArgumentException if the meeting is not found
     */
    @Override
    public Meeting findMeetingById(Integer meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NoSuchMeetingException("Meeting with ID " + meetingId + " not found."));
    }

    /**
     * meetingId로 ownerId를 찾는 메서드
     * @param meetingId The meeting ID
     * @return The owner ID
     * @throws IllegalArgumentException if the meeting or owner ID is not found
     */
    @Override
    public Integer getOwnerIdIdByMeetingId(Integer meetingId) {
        Meeting meeting = findMeetingById(meetingId);

        Team team = meeting.getTeam();
        if (team == null) {
            throw new IllegalArgumentException("Team associated with meeting ID " + meetingId + " not found.");
        }
        return team.getOwner().getUserId();
    }

    /**
     * 세션 ID를 미팅에 업데이트하는 메서드
     * @param meetingId The meeting ID
     * @param sessionId The session ID
     */
    @Override
    public void updateMeetingSessionId(Integer meetingId, String sessionId) {
        Meeting meeting = findMeetingById(meetingId);
        meeting.setSessionId(sessionId);
        meetingRepository.save(meeting);
    }

    @Override
    public void updateMeetingContent(Integer meetingId, String content) {
        Meeting meeting = findMeetingById(meetingId);
        meeting.setContent(content);
        meetingRepository.save(meeting);
    }

    /**
     * user가 팀장인지 확인하는 메서드
     * @param meetingId The meeting ID
     * @param userId The user ID
     */
    @Override
    public boolean isUserTeamLeader(Integer meetingId, Integer userId) {
        Integer ownerId = getOwnerIdIdByMeetingId(meetingId);
        return ownerId.equals(userId);
    }



    /**
     * 요청받은 정보를 이용해서 회의 객체 생성후 저장.
     *
     * @param meeting  requested Meeting dto. Must have teamId, name, starTime, endTime
     * @param username
     * @return 생성된 meeting id.
     */
    @Override
    @Transactional
    public int addMeeting(MeetingDto meeting, String username) {

        Team team = teamService.getTeamByIdAndOwnerUid(meeting.getTeam_id(),username);

        log.debug("creating meeting for userId : {}",username);
        log.debug("creating meeting : {}", meeting);

        Meeting newMeeting = Meeting.builder()
                .endAt(meeting.getEnd())
                .startAt(meeting.getStart())
                .team(team)
                .name(meeting.getName())
                .detail(meeting.getDetail())
                .build();

//        log.debug("creating new meeting : {}", newMeeting);
        Meeting savedMeeting = meetingRepository.save(newMeeting);
        List<User> userList = userTeamRepository.findUserByTeam(team);
        List<UserMeeting> userMeetingList = userList.stream().map((user) ->
                UserMeeting.builder()
                        .user(user)
                        .meeting(savedMeeting)
                        .build()
                )
                .toList();
        userMeetingRepository.saveAll(userMeetingList);
        return savedMeeting.getMeetingId();
    }

    @Override
    public MeetingDetailsDto findMeetingDetailById(int meetingId) {
        Meeting meeting = findMeetingById(meetingId);
        return MeetingDetailsDto.builder()
                .meeting_id(meeting.getMeetingId())
                .team_id(meeting.getTeam().getTeamId())
                .name(meeting.getName())
                .start_at(meeting.getStartAt())
                .end_at(meeting.getEndAt())
                .details(meeting.getDetail())
                .content(meeting.getContent())
                .created_at(meeting.getCreatedAt())
                .updated_at(meeting.getUpdatedAt())
                .session_id(meeting.getSessionId())
                .build();
    }

    @Override
    @Transactional
    public MeetingDetailsDto updateMeetingDetails(int meetingId, MeetingDto meetingDto, String username) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new NoSuchMeetingException("Meeting with ID " + meetingId + " not found."));

        if(!username.equals(meeting.getTeam().getOwner().getUid())) throw new NotTeamOwnerException();

        meeting.setDetail(meetingDto.getDetail());
        meeting.setName(meetingDto.getName());
        meeting.setStartAt(meetingDto.getStart());
        meeting.setEndAt(meetingDto.getEnd());
        Meeting savedMeeting = meetingRepository.save(meeting);


        return MeetingDetailsDto.builder()
                .meeting_id(savedMeeting.getMeetingId())
                .team_id(savedMeeting.getTeam().getTeamId())
                .name(savedMeeting.getName())
                .start_at(savedMeeting.getStartAt())
                .end_at(savedMeeting.getEndAt())
                .details(savedMeeting.getDetail())
                .content(savedMeeting.getContent())
                .created_at(savedMeeting.getCreatedAt())
                .updated_at(savedMeeting.getUpdatedAt())
                .session_id(savedMeeting.getSessionId())
                .build();
    }

    @Override
    @Transactional
    public void delete(int meetingId) {
        meetingRepository.deleteById(meetingId);
        return;
    }

    @Override
    public List<MeetingDetailsDto> findMeetingListByDto(MeetingListDto dto) {
        LocalDate today = dto.getToday().toLocalDate();
        List<Meeting> list = null;
        if(dto.isNext()) list = meetingRepository.findMeetingsByTeamIdAndStartAtAfter(dto.getTeamId(),today);
        else if(dto.isPrev()) list =  meetingRepository.findMeetingsByTeamIdAndStartAtBefore(dto.getTeamId(),today);
        else  list = meetingRepository.findMeetingsByTeamIdAndStartAt(dto.getTeamId(),today);

        if(list.isEmpty()) throw new NoSuchMeetingException("No meeting found for team id " + dto.getTeamId());
        return list.stream()
                .map((meeting -> MeetingDetailsDto.builder()
                        .meeting_id(meeting.getMeetingId())
                        .team_id(meeting.getTeam().getTeamId())
                        .name(meeting.getName())
                        .start_at(meeting.getStartAt())
                        .end_at(meeting.getEndAt())
                        .details(meeting.getDetail())
                        .content(meeting.getContent())
                        .created_at(meeting.getCreatedAt())
                        .updated_at(meeting.getUpdatedAt())
                        .session_id(meeting.getSessionId())
                        .build()))
                .toList();
    }

    @Override
    public String findMeetingIdBySessionId(String sessionId) {
        return meetingRepository.findMeetingIdBySessionId(sessionId);
    }
}
