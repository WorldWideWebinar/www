package com.ssafy.globalcc.domain.meeting.repository;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, String> {
    Optional<Meeting> findById(String meetingId);
}
