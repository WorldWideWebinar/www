package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTRequest;
import com.ssafy.globalcc.domain.meetingSTT.dto.response.MeetingSTTResponse;
import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;
import com.ssafy.globalcc.domain.meetingSTT.repository.MeetingSTTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MeetingSTTStompController {

    private final SimpMessagingTemplate template;
    private final MeetingSTTRepository meetingSTTRepository;
    private final MeetingRepository meetingRepository;

    @MessageMapping(value = "/stt/message")
    public void message(MeetingSTTRequest request) {
        Meeting meeting = meetingRepository.findById(Integer.valueOf(request.getMeetingId()))
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
        MeetingSTT meetingSTT = new MeetingSTT();
        meetingSTT.setMeeting(meeting);
        meetingSTT.setContent(request.getContent());

        meetingSTTRepository.save(meetingSTT);
        MeetingSTTResponse response = MeetingSTTResponse.of(request.getMeetingId(), request.getContent());
        template.convertAndSend("/sub/stt/room/" + request.getMeetingId(), response);
    }

}
