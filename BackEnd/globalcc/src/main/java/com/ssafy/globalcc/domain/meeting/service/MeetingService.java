package com.ssafy.globalcc.domain.meeting.service;

import com.ssafy.globalcc.domain.meeting.dto.MeetingDetailsDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingDto;
import com.ssafy.globalcc.domain.meeting.dto.MeetingListDto;
import com.ssafy.globalcc.domain.meeting.entity.Meeting;

import java.util.List;

public interface MeetingService {

    Meeting findMeetingById(Integer meetingId);

    Integer getOwnerIdIdByMeetingId(Integer meetingId);

    void updateMeetingSessionId(Integer meetingId, String sessionId);

    boolean isUserTeamLeader(Integer meetingId, Integer userId);

    int addMeeting(MeetingDto meeting, String username);

    MeetingDetailsDto findMeetingDetailById(int meetingId);

    MeetingDetailsDto updateMeetingDetails(int meetingId, MeetingDto meetingDto, String username);

    void delete(int meetingId);

    List<MeetingDetailsDto> findMEetingListByDto(MeetingListDto dto);
}
