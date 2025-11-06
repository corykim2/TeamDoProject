package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {
    Optional<SessionEntity> findBySessionId(String sessionId);
    void deleteByUserEntity_Id(String userId);
}
