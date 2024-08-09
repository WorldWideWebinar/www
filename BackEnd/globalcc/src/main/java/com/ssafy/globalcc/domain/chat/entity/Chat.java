package com.ssafy.globalcc.domain.chat.entity;

import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "team_id")
    private Team teamId;

    @Column(name = "sender_profile")
    private String senderProfile;

    public enum ContentType {
        text,
        image,
        file
    }
}
