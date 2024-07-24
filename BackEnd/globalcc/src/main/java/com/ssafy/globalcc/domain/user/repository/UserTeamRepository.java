package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.user.entity.UserTeam;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {
    @Query("SELECT ut.team.teamId FROM UserTeam ut WHERE ut.user.userId = :userId")
    List<Integer> findUserTeamIdsByUserUserId(@Param("userId") Integer userId);

}
