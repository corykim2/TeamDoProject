package com.TeamAA.TeamDo.service.User;

import com.TeamAA.TeamDo.controller.exceptionhandler.InvalidCredentialsException;
import com.TeamAA.TeamDo.controller.exceptionhandler.SessionExpiredException;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionService {

    private final UserRepository userRepository;

    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String getUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new SessionExpiredException("로그인 세션이 유효하지 않습니다.");
        }
        return userId;
    }
    public UserEntity findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

}
