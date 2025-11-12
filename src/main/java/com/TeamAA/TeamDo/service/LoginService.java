package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.entity.SessionEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    public LoginResponse login(LoginRequest request) {
        //사용자 조회
        UserEntity user = userService.findById(request.getId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        //비밀번호 검증
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //기존 세션이 있으면 제거 (동시 로그인 방지)
        sessionService.deleteSessionByUserId(user.getId());

        //새로운 세션 생성 (DB 저장)
        SessionEntity newSession = sessionService.createSession(user);

        // 클라이언트로 세션 ID 반환
        return new LoginResponse(
                newSession.getSessionId(),
                user.getId(),
                "로그인 성공"
        );
    }
}
