package com.TeamAA.TeamDo.repository.Team;

import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamParticipatingRepository extends JpaRepository<TeamParticipatingEntity, Long> {
    boolean existsByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);
    Optional<TeamParticipatingEntity> findByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);
    void deleteByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);
}
