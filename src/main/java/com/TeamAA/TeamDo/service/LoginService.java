package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.entity.SessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userService.findById(request.getId());

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 기존 세션 삭제 후 새로 발급
        sessionService.deleteSessionByUserId(user.getId());
        SessionEntity session = sessionService.createSession(user);

        return new LoginResponse(session.getSessionId(), user.getId(), "로그인 성공");
    }
}
