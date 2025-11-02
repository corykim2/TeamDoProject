package com.TeamAA.TeamDo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TeamAA.TeamDo.entity.Team;

@Repository  // Optional, JpaRepository를 상속하면 자동 인식되지만 명시하면 확실
public interface TeamRepository extends JpaRepository<Team, Long> {
}
