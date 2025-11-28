package com.TeamAA.TeamDo.service.User;

import com.TeamAA.TeamDo.controller.exceptionhandler.InvalidCredentialsException;
import com.TeamAA.TeamDo.controller.exceptionhandler.WithdrawnUserException;
import com.TeamAA.TeamDo.dto.User.LoginRequest;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private SignupService signupService;

    // 사용자 조회,존재하지 않는 아이디 예외처리

    public UserEntity login(LoginRequest request) {
        try {
            UserEntity user;

            // 아이디 존재 여부 확인
            try {
                user = signupService.findById(request.getId());
            } catch (Exception e) {
                // 아이디 없음 → 401
                throw new InvalidCredentialsException("아이디 또는 비밀번호가 잘못 되었습니다.");
            }

            // 탈퇴 계정
            if (user.isWithdrawn()) {
                throw new WithdrawnUserException("탈퇴한 사용자입니다."); // 403
            }

            // 비밀번호 불일치
            if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("아이디 또는 비밀번호가 잘못 되었습니다."); // 401
            }

            return user;

        } catch (WithdrawnUserException | InvalidCredentialsException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("로그인 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}
