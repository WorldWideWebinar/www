package com.ssafy.globalcc.domain.chat.controller;

import com.ssafy.globalcc.domain.chat.dto.response.ChatResponse;
import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/team/{teamId}")
    public List<ChatResponse> getChatsByTeamId(@PathVariable Integer teamId) {
        List<Chat> chats = chatService.getChatsByTeamId(teamId);
        return chats.stream()
                .map(chat -> ChatResponse.builder()
                        .senderId(chat.getSenderId().getUserId())
                        .teamId(chat.getTeamId().getTeamId())
                        .content(chat.getContent())
                        .contentType(chat.getContentType().name())
                        .createdAt(ZonedDateTime.parse(chat.getCreatedAt().toString()))
                        .senderProfile(chat.getSenderId().getProfileImage())
                        .build())
                .collect(Collectors.toList());
    }
}