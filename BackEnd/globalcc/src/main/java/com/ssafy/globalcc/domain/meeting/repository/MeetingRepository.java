package com.ssafy.globalcc.domain.meeting.repository;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    Optional<Meeting> findById(Integer meetingId);
    List<Meeting> findAllByTeam(Team team);
}
