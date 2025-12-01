package com.TeamAA.TeamDo.repository.Team;

import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamParticipatingRepository extends JpaRepository<TeamParticipatingEntity, Long> {

    boolean existsByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);

    List<TeamParticipatingEntity> findByUserEntity(UserEntity userEntity);

    void deleteByUserEntityAndTeamEntity(UserEntity userEntity, TeamEntity teamEntity);

    boolean existsByTeamEntity_IdAndUserEntity_Id(Long teamId, String userId);

    @Query("SELECT tp.teamEntity FROM TeamParticipatingEntity tp WHERE tp.userEntity.id = :userId")
    List<TeamEntity> findTeamsByUserEntity_Id(@Param("userId") String userId);
}
