package com.TeamAA.TeamDo.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionService {

    public String getUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("로그인 세션이 유효하지 않습니다.");
        }
        return userId;
    }
}
