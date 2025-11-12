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
        // 세션 검증 + 유저 조회
        UserEntity user = sessionService.validateAndGetUser(sessionId);

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
