package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/logout")
    public String logout(@RequestHeader("Session-Id") String sessionId) {
        boolean deleted = sessionService.deleteSessionBySessionId(sessionId);

        if (deleted) {
            return "로그아웃 완료 (DB 세션 삭제)";
        } else {
            return "유효하지 않은 세션입니다.";
        }
    }
}