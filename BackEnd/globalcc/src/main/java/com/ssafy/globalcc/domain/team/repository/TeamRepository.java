package com.ssafy.globalcc.domain.team.repository;

import com.ssafy.globalcc.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
