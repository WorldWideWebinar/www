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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatStompController {
    private final RabbitTemplate rabbitTemplate;
    private final TeamService teamService;
    private final UserService userService;
    private final ChatService chatService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "chat.key";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @MessageMapping("chat.{teamId}")
    public void share(
            @DestinationVariable("teamId") final Integer teamId,
            @Payload final ChatRequest request
    ) {
        String redisKey = "Chat:" + teamId;
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String redisValue = String.format("%s#%s#%s", request.getSenderId(), timestamp, request.getContent());
        redisTemplate.opsForList().rightPush(redisKey, redisValue);

        ChatResponse response = ChatResponse.of(request.getSenderId(), request.getTeamId(), request.getContent(), request.getSenderProfile());
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME, ROUTING_KEY + teamId, response
        );
    }

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
                                String[] parts = message.split("#", 3);
                                log.debug("-------Redis Test-------");
                                log.debug(parts[0] + " # " + parts[1] + " # " + parts[2]);
                                log.debug("------------------------");
                                if (parts.length < 3) {
                                    throw new IllegalArgumentException("Invalid message format");
                                }
                                Integer senderId = Integer.valueOf(parts[0]);
                                String timestampStr = parts[1];
                                String content = parts[2];

                                LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DATE_TIME_FORMATTER);
                                User user = userService.findUserById(senderId);

                                Chat chat = new Chat();
                                chat.setTeamId(team);
                                chat.setSenderId(user);
                                chat.setContent(content);
                                chat.setContentType(Chat.ContentType.TEXT);
                                chat.setCreatedAt(timestamp);
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
