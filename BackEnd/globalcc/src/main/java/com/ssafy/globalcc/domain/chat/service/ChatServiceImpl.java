package com.ssafy.globalcc.domain.chat.service;

import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;

    @Override
    public void saveAllChats(List<Chat> chatList) {
        chatRepository.saveAll(chatList);
    }
}
