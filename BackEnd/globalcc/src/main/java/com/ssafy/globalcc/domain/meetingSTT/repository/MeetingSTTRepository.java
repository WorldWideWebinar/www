package com.ssafy.globalcc.domain.meetingSTT.repository;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meetingSTT.entity.MeetingSTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingSTTRepository extends JpaRepository<MeetingSTT, Integer> {
    List<MeetingSTT> findByMeeting(Meeting meeting);
    List<MeetingSTT> findByMeetingMeetingId(Integer meetingId);
}
