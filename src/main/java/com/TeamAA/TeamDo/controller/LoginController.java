package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {

        // 사용자 인증
        UserEntity user = loginService.login(request);

        // HttpSession에 사용자 ID 저장
        session.setAttribute("userId", user.getId());

        return new LoginResponse(
                session.getId(),  // 세션 ID는 HttpSession에서 자동 생성
                user.getId(),
                "로그인 성공"
        );
    }
}
