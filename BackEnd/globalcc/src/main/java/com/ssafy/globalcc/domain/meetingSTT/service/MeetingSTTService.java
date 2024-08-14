package com.ssafy.globalcc.domain.meetingSTT.service;

import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;

import java.util.List;

public interface MeetingSTTService {
    List<MeetingSTT> getMeetingSTTsByMeetingId(Integer meetingId);
}
