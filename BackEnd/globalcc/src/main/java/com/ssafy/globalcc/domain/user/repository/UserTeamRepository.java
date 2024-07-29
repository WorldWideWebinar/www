package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.entity.UserTeam;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {
    @Query("SELECT ut.team.teamId FROM UserTeam ut WHERE ut.user.userId = :userId")
    List<Integer> findUserTeamIdsByUserUserId(@Param("userId") Integer userId);
    List<Integer> findUserIdByTeam(Team team);
    @Modifying
    @Query("select  u from User u join UserTeam ut on ut.user.userId = u.userId where ut.team = :team")
    List<User> findUserByTeam(@Param("team") Team team);
    @Modifying
    @Query("delete from UserTeam ut where ut.user.userId=:userId AND ut.team.teamId=:teamId")
    int deleteByUserUserId(@Param("userId") Integer userId, @Param("teamId") Integer teamId);
}
