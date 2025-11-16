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
        //정상 처리 로직
        try {
            String userId = sessionService.getUserId(session); //세션검증
            session.invalidate();
            return ResponseEntity.ok("로그아웃 완료");
        } catch (Exception e){
            // 내부 서버 예외
            throw new RuntimeException("로그아웃 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}