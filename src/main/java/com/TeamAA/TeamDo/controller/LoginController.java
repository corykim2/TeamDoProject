package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.entity.SessionEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.LoginService;
import com.TeamAA.TeamDo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        // 사용자 인증
        LoginResponse response = loginService.login(request);

        // DB 세션 생성
        UserEntity user = new UserEntity();
        user.setId(response.getUserId());
        SessionEntity session = sessionService.createSession(user);

        // 세션 ID를 응답으로 반환
        response.setSessionId(session.getSessionId());

        return response;
    }
}
