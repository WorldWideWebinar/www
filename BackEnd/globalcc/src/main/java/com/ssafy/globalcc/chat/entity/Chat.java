package com.ssafy.globalcc.chat.entity;

import com.ssafy.globalcc.team.entity.Team;
import com.ssafy.globalcc.user.entity.User;
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

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "team_id", insertable=false, updatable=false)
    private Team team;

    public enum ContentType {
        TEXT,
        IMAGE,
        FILE
    }
}
