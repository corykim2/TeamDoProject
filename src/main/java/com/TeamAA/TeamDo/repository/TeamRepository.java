package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    Optional<TeamEntity> findByInviteCode(String inviteCode);
}
