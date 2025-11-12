package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.SessionEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    // 세션 생성
    public SessionEntity createSession(UserEntity userEntity) {
        String sessionId = UUID.randomUUID().toString();
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp expires = Timestamp.from(Instant.now().plusSeconds(60 * 30)); // 30분 유효

        SessionEntity session = new SessionEntity(sessionId, userEntity, now, expires);
        return sessionRepository.save(session);
    }

    // 세션 조회
    public Optional<SessionEntity> getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    // 세션 삭제 (SessionId 기준)
    public boolean deleteSessionBySessionId(String sessionId) {
        Optional<SessionEntity> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            sessionRepository.delete(sessionOpt.get());
            return true;
        }
        return false;
    }

    // id 검색후 삭제
    public void deleteSessionByUserId(String userId) {
        sessionRepository.deleteByUserEntity_Id(userId);
    }
    //세션 검증 
    public UserEntity validateAndGetUser(String sessionId) {
        SessionEntity session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 세션입니다. 다시 로그인해주세요."));

        UserEntity user = session.getUserEntity();
        if (user == null) {
            throw new IllegalArgumentException("세션에 연결된 사용자를 찾을 수 없습니다.");
        }

        return user;
    }
}
