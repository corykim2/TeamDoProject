package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.SessionEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeleteUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    @Transactional
    public void deleteUser(String sessionId, String password) {
        // 세션 검증
        SessionEntity session = sessionService.getSession(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 세션입니다. 다시 로그인해주세요."));

        UserEntity user = session.getUserEntity();
        if (user == null) {
            throw new IllegalArgumentException("세션에 연결된 사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 검증
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 탈퇴 처리 (withdrawn = true)
        user.setWithdrawn(true);
        userRepository.save(user);

        // 해당 유저의 모든 세션 삭제
        sessionService.deleteSessionByUserId(user.getId());
    }
}
