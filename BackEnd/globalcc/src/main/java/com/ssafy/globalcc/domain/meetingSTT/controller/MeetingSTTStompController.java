package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTRequest;
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

    @MessageMapping("meetingSTT.{meetingId}")
    public void share(
            @DestinationVariable("meetingId") final Integer meetingId,
            @Payload final MeetingSTTRequest request
    ) {
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + meetingId, request
        );
    }
}

