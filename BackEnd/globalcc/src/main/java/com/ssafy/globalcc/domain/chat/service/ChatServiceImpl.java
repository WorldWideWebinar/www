package com.ssafy.globalcc.domain.chat.service;

import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.chat.repository.ChatRepository;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final TeamService teamService;

    @Override
    public List<Chat> getChatsByTeamId(Integer teamId) {
        Team team = teamService.findTeamById(teamId);
        return chatRepository.findByTeamId(team);
    }

    @Override
    public void saveAllChats(List<Chat> chatList) {
        chatRepository.saveAll(chatList);
    }
}
