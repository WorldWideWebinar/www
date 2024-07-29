package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.user.entity.UserMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMeetingRepository extends JpaRepository<UserMeeting, Integer> {
}
