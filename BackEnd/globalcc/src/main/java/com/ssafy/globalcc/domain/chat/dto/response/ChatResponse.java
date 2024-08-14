package com.ssafy.globalcc.domain.chat.dto.response;

import lombok.*;

import java.time.ZonedDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private int senderId;
    private int teamId;
    private String content;
    private String contentType;
    private ZonedDateTime createdAt;
    private String senderProfile;

    public static ChatResponse of(Integer senderId, Integer teamId, String content, String senderProfile) {
        return ChatResponse.builder()
                .senderId(senderId)
                .teamId(teamId)
                .content(content)
                .contentType("text")
                .createdAt(ZonedDateTime.now())
                .senderProfile(senderProfile)
                .build();
    }
}
