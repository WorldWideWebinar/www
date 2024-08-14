package com.ssafy.globalcc.domain.meetingSTT.service;

import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.service.MeetingService;
import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;
import com.ssafy.globalcc.domain.meetingSTT.repository.MeetingSTTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingSTTServiceImpl implements MeetingSTTService {
    private final MeetingSTTRepository meetingSTTRepository;
    private final MeetingService meetingService;

    @Override
    public List<MeetingSTT> getMeetingSTTsByMeetingId(Integer meetingId) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        return meetingSTTRepository.findByMeeting(meeting);
    }
}
