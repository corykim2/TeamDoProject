package com.TeamAA.TeamDo.Interceptor;

import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return false;
        }

        // 사용자 정보 조회 및 저장
        String id = (String) session.getAttribute("userId");
        UserEntity user = sessionService.findById(id); // 사용자 조회 로직
        request.setAttribute("loginUser", user); // 이후 컨트롤러에서 꺼내 쓸 수 있음

        return true;
    }
}
