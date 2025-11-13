package com.TeamAA.TeamDo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // HttpSession 기반 로그아웃
        return "로그아웃 완료";
    }
}