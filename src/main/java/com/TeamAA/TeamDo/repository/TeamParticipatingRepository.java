package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamParticipatingRepository extends JpaRepository<TeamParticipatingEntity, Long> {

    boolean existsByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);

    Optional<TeamParticipatingEntity> findByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);
}
