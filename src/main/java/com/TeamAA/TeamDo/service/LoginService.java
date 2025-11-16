package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.controller.exceptionhandler.WithdrawnUserException;
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

            // 입력데이터 부족 예외처리
            if (request.getId() == null || request.getId().isBlank()) {
                throw new IllegalArgumentException("아이디를 입력해 주세요.");
            }

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("비밀번호를 입력해 주세요.");
            }

            //데이터 범위 초과 예외처리
            if (request.getId().length() >= 30) {
                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못 되었습니다.");
            }

            if (request.getPassword().length() < 5 || request.getPassword().length() >= 16) {
                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못 되었습니다.");
            }

            // 사용자 조회,존재하지 않는 아이디 예외처리
        try{
            UserEntity user = signupService.findById(request.getId());

            // 탈퇴 확인 예외처리
            if (user.isWithdrawn()) {
                throw new WithdrawnUserException("탈퇴한 사용자입니다."); // 글로벌 핸들러에서 403 처리
            }

            // 비밀번호 검증 예외처리 (해싱)
            if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못 되었습니다.");
            }
            return user;
        } catch(IllegalArgumentException e) {
            throw e;

        } catch(WithdrawnUserException e) {
            throw e;

        } catch(Exception e){
                throw new RuntimeException("로그인 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}
