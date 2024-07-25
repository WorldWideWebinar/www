package com.ssafy.globalcc.domain.user.entity;

import com.ssafy.globalcc.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_team")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_time")
    private Instant lastTime;

    @ColumnDefault("1")
    @Generated
    @Column(name = "admission")
    private Boolean admission;

}