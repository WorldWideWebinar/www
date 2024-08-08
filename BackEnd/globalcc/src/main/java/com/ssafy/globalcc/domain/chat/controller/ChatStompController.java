package com.ssafy.globalcc.domain.chat.controller;

import com.ssafy.globalcc.domain.chat.dto.request.ChatRequest;
import com.ssafy.globalcc.domain.chat.dto.response.ChatResponse;
import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.chat.service.ChatService;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.service.TeamService;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.service.UserService;
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
    private final UserService userService;
    private final ChatService chatService;

    private static final String EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "chat.key";
    private final RedisTemplate<String, String> redisTemplate;

    @MessageMapping("chat.{teamId}")
    public void share(
            @DestinationVariable("teamId") final Integer teamId,
            @Payload final ChatRequest request
    ) {
        String redisKey = "Chat:" + teamId;
        String redisValue = request.getSenderId() + ":" + request.getContent();
        redisTemplate.opsForList().rightPush(redisKey, redisValue);

        ChatResponse response = ChatResponse.of(request.getSenderId(), request.getTeamId(), request.getContent(), request.getSenderProfile());
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + teamId, response
        );
    }

    //DB 저장 로직 추가
    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    public void saveToDatabase() {
        Set<String> keys = redisTemplate.keys("Chat:*");
//        redisTemplate.delete(keys);
        if (keys != null) {
            for (String key : keys) {
                List<String> messageList = redisTemplate.opsForList().range(key, 0, -1);
                if (messageList != null && !messageList.isEmpty()) {
                    Integer teamId = extractTeamId(key);
                    Team team = teamService.findTeamById(teamId);

                    List<Chat> chatList = messageList.stream()
                            .map(message -> {
                                String[] parts = message.split(":", 2);
                                Integer senderId = Integer.valueOf(parts[0]);
                                User user = userService.findUserById(senderId);
                                String content = parts[1];

                                Chat chat = new Chat();
                                chat.setTeamId(team);
                                chat.setSenderId(user);
                                chat.setContent(content);
                                chat.setContentType(Chat.ContentType.valueOf("TEXT"));
                                return chat;
                            })
                            .toList();
                    chatService.saveAllChats(chatList);

                    redisTemplate.delete(key);
                }
            }
        }
    }

    private Integer extractTeamId(String key) {
        String[] parts = key.split(":");
        if (parts.length > 1) {
            try {
                return Integer.valueOf(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid teamId in Redis key");
            }
        }
        throw new IllegalArgumentException("Invalid Redis key format");
    }
}

