package com.ssafy.globalcc.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("chat.{teamId}")
    public void share(
            @DestinationVariable("teamId") final Integer teamId,
            @Payload final ChatRequest request
    ) {
        String redisKey = "Chat:" + teamId;
        String timestamp = ZonedDateTime.now().toString();
        System.out.println("---------Chat Time----------");
        System.out.println(timestamp);
        System.out.println("----------------------------");

        Map<String, String> chatData = new HashMap<>();
        chatData.put("senderId", String.valueOf(request.getSenderId()));
        chatData.put("timestamp", timestamp);
        chatData.put("content", request.getContent());
        chatData.put("senderProfile", request.getSenderProfile());

        try {
            String redisValue = objectMapper.writeValueAsString(chatData);
            redisTemplate.opsForList().rightPush(redisKey, redisValue);
        } catch (Exception e) {
            log.error("Error serializing chat data", e);
        }

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
                                try {
                                    Map<String, String> chatData = objectMapper.readValue(message, Map.class);

                                    Integer senderId = Integer.valueOf(chatData.get("senderId"));
                                    String timestampStr = chatData.get("timestamp");
                                    String content = chatData.get("content");
                                    String senderProfile = chatData.get("senderProfile");

                                    log.debug("------------Redis Test------------");
                                    log.debug(senderId + ":" + timestampStr + ":" + content + ":" + senderProfile);
                                    log.debug("----------------------------------");

                                    ZonedDateTime timestamp = ZonedDateTime.parse(timestampStr);
                                    User user = userService.findUserById(senderId);

                                    Chat chat = new Chat();
                                    chat.setTeamId(team);
                                    chat.setSenderId(user);
                                    chat.setContent(content);
                                    chat.setContentType(Chat.ContentType.text);
                                    chat.setSenderProfile(senderProfile);
                                    chat.setCreatedAt(timestamp);
                                    return chat;
                                } catch (Exception e) {
                                    log.error("Error deserializing chat data", e);
                                    throw new RuntimeException("Failed to deserialize message", e);
                                }
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
