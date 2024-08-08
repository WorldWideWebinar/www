package com.ssafy.globalcc.domain.chat.repository;

import com.ssafy.globalcc.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
}

