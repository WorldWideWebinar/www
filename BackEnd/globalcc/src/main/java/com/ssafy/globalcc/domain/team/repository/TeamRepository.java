package com.ssafy.globalcc.domain.team.repository;

import com.ssafy.globalcc.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
//    @Query("select distinct t.owner.userId, t.name, ut.user.userId, m.meetingId, t.emoji, t.createdAt " +
//            "from Team t " +
//            "left join UserTeam ut " +
//            "on ut.team.teamId = t.teamId " +
//            "left join Meeting m " +
//            "on m.team = t " +
//            "where t.teamId = :id")
//    TeamDetailResult findTeamDetailsByTeamId(Integer id);
}
