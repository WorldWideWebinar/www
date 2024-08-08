package com.ssafy.globalcc.domain.chat.service;

import com.ssafy.globalcc.domain.chat.entity.Chat;

import java.util.List;

public interface ChatService {
    List<Chat> getChatsByTeamId(Integer teamId);
    void saveAllChats(List<Chat> chatList);
}
