package com.ssafy.globalcc.domain.meeting.repository;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.team.entity.Team;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    Optional<Meeting> findById(Integer meetingId);
    List<Meeting> findAllByTeam(Team team);
    @Query("select m from  Meeting m where m.team.teamId = :teamId and DATE(m.startAt) < :today")
    List<Meeting> findMeetingsByTeamIdAndStartAtBefore(@Param("teamId") Integer teamId, @Param("today") LocalDate today);
//    List<Meeting> findMeetingsByTeamIdAndStartAtBefore(Integer teamId, LocalDate today);
    @Query("select m from  Meeting m where m.team.teamId = :teamId and DATE(m.startAt) > :today")
    List<Meeting> findMeetingsByTeamIdAndStartAtAfter(@Param("teamId") Integer teamId, @Param("today") LocalDate today);
    @Query("select m from  Meeting m where m.team.teamId = :teamId and DATE(m.startAt) = :today")
    List<Meeting> findMeetingsByTeamIdAndStartAt(@Param("teamId") Integer teamId, @Param("today") LocalDate today);
}
