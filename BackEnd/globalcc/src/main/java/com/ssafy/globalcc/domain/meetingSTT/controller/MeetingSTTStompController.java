package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTRequest;
import com.ssafy.globalcc.domain.meetingSTT.dto.request.MeetingSTTSegment;
import com.ssafy.globalcc.domain.meetingSTT.dto.response.MeetingSTTResponse;
import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;
import com.ssafy.globalcc.domain.meetingSTT.repository.MeetingSTTRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MeetingSTTStompController {

    private static final Logger log = LoggerFactory.getLogger(MeetingSTTStompController.class);
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "meetingSTT.exchange";
    private static final String ROUTING_KEY = "meetingSTT.key";
    private final MeetingSTTRepository meetingSTTRepository;
    private final MeetingRepository meetingRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final float MIN_LAST = -1f;
    @MessageMapping("meetingSTT.{meetingId}")
    public void share(
            @DestinationVariable("meetingId") final Integer meetingId,
            @Payload final MeetingSTTRequest request
    ) {
        //meetingSTTRepository.save(meetingSTT); //성능 개선 위해 Redis 연결
        String redisKey = "MeetingSTT:" + meetingId;
        String redisKeyForLastSegmentTime = "MeetingSTT_lastSegmentTime:" + meetingId;
        String redisLastSegmentTime = redisTemplate.opsForValue().get(redisKeyForLastSegmentTime);
        float lastSegmentTime = MIN_LAST;
        if(redisLastSegmentTime != null) {
            lastSegmentTime = Float.parseFloat(redisLastSegmentTime);
        }
        log.info("lastSegmentTime : {}", lastSegmentTime);
        for(int i = 0; i < request.getSegments().size(); i++){
            MeetingSTTSegment segment = request.getSegments().get(i);
            log.info("segment: {}" , segment);
            redisTemplate.opsForValue().set(redisKey + String.valueOf(segment.getStart()), segment.getText());
        }

//        redisTemplate.opsForList().rightPush(redisKey, request.getContent());
        log.info("received request: {}", request);
        MeetingSTTResponse response = MeetingSTTResponse.of(request.getMeetingId(), request.getContent(),request.getSegments());
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + meetingId, response
        );
    }
    
    //Redis에서 DB 저장
    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    public void saveToDatabase() {
        Set<String> keys = redisTemplate.keys("MeetingSTT:*");
        if (keys != null) {
            for (String key : keys) {
                List<String> messageList = redisTemplate.opsForList().range(key, 0, -1);
                if (messageList != null && !messageList.isEmpty()) {
                    Integer meetingId = extractMeetingId(key);
                    Meeting meeting = meetingRepository.findById(meetingId)
                            .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

                    List<MeetingSTT> meetingSTTList = messageList.stream()
                            .map(content -> {
                                MeetingSTT meetingSTT = new MeetingSTT();
                                meetingSTT.setMeeting(meeting);
                                meetingSTT.setContent(content);
                                return meetingSTT;
                            })
                            .toList();
                    meetingSTTRepository.saveAll(meetingSTTList);

                    redisTemplate.delete(key);
                }
            }
        }
    }

    private Integer extractMeetingId(String key) {
        String[] parts = key.split(":");
        if (parts.length > 1) {
            try {
                return Integer.valueOf(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid meetingId in Redis key");
            }
        }
        throw new IllegalArgumentException("Invalid Redis key format");
    }
}

