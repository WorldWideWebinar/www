package com.ssafy.globalcc.domain.chat.repository;

import com.ssafy.globalcc.domain.chat.entity.Chat;
import com.ssafy.globalcc.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByTeamId(Team team);
}

