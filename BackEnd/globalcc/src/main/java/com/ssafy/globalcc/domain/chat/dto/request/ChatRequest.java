package com.ssafy.globalcc.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private int senderId;
    private int teamId;
    private String content;
    private String contentType;
}
