package com.ssafy.globalcc.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "language", nullable = false)
    private String language = "eng";

    @Column(name = "profile_image", length = 2048)
    private String profileImage;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
