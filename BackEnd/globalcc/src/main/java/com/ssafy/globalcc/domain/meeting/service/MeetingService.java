package com.ssafy.globalcc.domain.meeting.service;

import com.ssafy.globalcc.domain.meeting.entity.Meeting;
import com.ssafy.globalcc.domain.meeting.repository.MeetingRepository;
import com.ssafy.globalcc.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    /**
     * meetingId로 미팅을 조회하는 공통 메서드
     * @param meetingId The meeting ID
     * @return The Meeting entity
     * @throws IllegalArgumentException if the meeting is not found
     */
    public Meeting findMeetingById(Integer meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting with ID " + meetingId + " not found."));
    }

    /**
     * meetingId로 ownerId를 찾는 메서드
     * @param meetingId The meeting ID
     * @return The owner ID
     * @throws IllegalArgumentException if the meeting or owner ID is not found
     */
    public Integer getOwnerIdIdByMeetingId(Integer meetingId) {
        Meeting meeting = findMeetingById(meetingId);

        Team team = meeting.getTeam();
        if (team == null) {
            throw new IllegalArgumentException("Team associated with meeting ID " + meetingId + " not found.");
        }
        return team.getOwner().getUserId();
    }

    /**
     * 세션 ID를 미팅에 업데이트하는 메서드
     * @param meetingId The meeting ID
     * @param sessionId The session ID
     */
    public void updateMeetingSessionId(Integer meetingId, String sessionId) {
        Meeting meeting = findMeetingById(meetingId);
        meeting.setSessionId(sessionId);
        meetingRepository.save(meeting);
    }

    /**
     * user가 팀장인지 확인하는 메서드
     * @param meetingId The meeting ID
     * @param userId The user ID
     */
    public boolean isUserTeamLeader(Integer meetingId, Integer userId) {
        Integer ownerId = getOwnerIdIdByMeetingId(meetingId);
        return ownerId.equals(userId);
    }
}
