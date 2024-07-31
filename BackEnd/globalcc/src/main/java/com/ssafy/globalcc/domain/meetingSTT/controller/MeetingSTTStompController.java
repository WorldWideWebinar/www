package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTRequest;
import com.ssafy.globalcc.domain.meetingSTT.dto.response.MeetingSTTResponse;
import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;
import com.ssafy.globalcc.domain.meetingSTT.repository.MeetingSTTRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MeetingSTTStompController {
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "meetingSTT.exchange";
    private static final String ROUTING_KEY = "meetingSTT.key";
    private final MeetingSTTRepository meetingSTTRepository;
    private final MeetingRepository meetingRepository;

    @MessageMapping("meetingSTT.{meetingId}")
    public void share(
            @DestinationVariable("meetingId") final Integer meetingId,
            @Payload final MeetingSTTRequest request
    ) {
        Meeting meeting = meetingRepository.findById(Integer.valueOf(request.getMeetingId()))
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
        MeetingSTT meetingSTT = new MeetingSTT();
        meetingSTT.setMeeting(meeting);
        meetingSTT.setContent(request.getContent());
        meetingSTTRepository.save(meetingSTT);

        MeetingSTTResponse response = MeetingSTTResponse.of(meeting.getMeetingId(), request.getContent());
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + meetingId, response
        );
    }
}

