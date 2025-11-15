package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private SignupService signupService;

    public UserEntity login(LoginRequest request) {
        // 사용자 조회
        UserEntity user = signupService.findById(request.getId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 해싱 비밀번호 검증
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
