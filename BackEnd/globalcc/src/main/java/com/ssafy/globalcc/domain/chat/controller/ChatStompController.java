package com.ssafy.globalcc.domain.chat.controller;

import com.ssafy.globalcc.domain.chat.dto.request.ChatRequest;
import com.ssafy.globalcc.domain.chat.dto.response.ChatResponse;
import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
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
public class ChatStompController {
    private final RabbitTemplate rabbitTemplate;
    private final TeamService teamService;

    private static final String EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "chat.key";
    private final RedisTemplate<String, String> redisTemplate;

    @MessageMapping("chat.{teamId}")
    public void share(
            @DestinationVariable("teamId") final Integer teamId,
            @Payload final ChatRequest request
    ) {
        String redisKey = "Chat:" + teamId;
        redisTemplate.opsForList().rightPush(redisKey, request.getContent());

        ChatResponse response = ChatResponse.of(request.getSenderId(), request.getTeamId(), request.getContent(), request.getSenderProfile());
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + teamId, response
        );
    }

//    DB 저장 로직 추가
//    @Scheduled(fixedDelay = 60000) // 60초마다 실행
//    public void saveToDatabase() {
//        Set<String> keys = redisTemplate.keys("Chat:*");
//        if (keys != null) {
//            for (String key : keys) {
//                List<String> messageList = redisTemplate.opsForList().range(key, 0, -1);
//                if (messageList != null && !messageList.isEmpty()) {
//                    Integer teamId = extractTeamId(key);
//                    Team team = teamService.findTeamById(teamId);
//
//                    List<Chat> chatList = messageList.stream()
//                            .map(content -> {
//                                Chat chat = new Chat();
//                                chat.setTeamId(team);
//                                chat.setContent(content);
//                                return chat;
//                            })
//                            .toList();
//                    chatRepository.saveAll(chatList); // chatRepository 추가 필요
//
//                    redisTemplate.delete(key);
//                }
//            }
//        }
//    }
//
//    private Integer extractTeamId(String key) {
//        String[] parts = key.split(":");
//        if (parts.length > 1) {
//            try {
//                return Integer.valueOf(parts[1]);
//            } catch (NumberFormatException e) {
//                throw new IllegalArgumentException("Invalid teamId in Redis key");
//            }
//        }
//        throw new IllegalArgumentException("Invalid Redis key format");
//    }
}

