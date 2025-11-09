package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
