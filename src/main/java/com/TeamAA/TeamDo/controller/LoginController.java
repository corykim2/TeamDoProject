package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        UserEntity user = loginService.login(request);
        session.setAttribute("userId", user.getId()); //세션생성 및 아이디 매칭
        LoginResponse response = new LoginResponse("로그인완료");
        return ResponseEntity.ok(response);
    }
}
