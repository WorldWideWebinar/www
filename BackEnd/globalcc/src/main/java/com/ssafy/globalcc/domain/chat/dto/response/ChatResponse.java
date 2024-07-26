package com.ssafy.globalcc.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChatResponse {
    private int senderId;
    private int teamId;
    private String content;
    private String contentType;
    private Timestamp createdAt;
}
