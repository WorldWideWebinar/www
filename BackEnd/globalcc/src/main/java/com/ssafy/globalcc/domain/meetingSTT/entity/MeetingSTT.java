package com.ssafy.globalcc.domain.meetingSTT.entity;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "meeting_stt")
@Data
@NoArgsConstructor
public class MeetingSTT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_stt_id")
    private Integer meetingSttId;

    @ManyToOne
    @JoinColumn(name = "meeting_id", referencedColumnName = "meeting_id")
    private Meeting meeting;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
}
