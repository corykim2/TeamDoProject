package com.TeamAA.TeamDo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamAA.TeamDo.service.SessionService;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        String userId = sessionService.getUserId(session); //세션검증
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }
}