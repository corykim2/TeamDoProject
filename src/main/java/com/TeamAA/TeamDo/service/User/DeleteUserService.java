package com.TeamAA.TeamDo.service.User;

import com.TeamAA.TeamDo.controller.exceptionhandler.InvalidCredentialsException;
import com.TeamAA.TeamDo.dto.User.DeleteUserRequest;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserEntity deleteUser(String id, DeleteUserRequest request) {

        // 사용자 조회
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 불일치 예외 처리
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다, 올바른 비밀번호를 입력해주세요.");
        }

        // 탈퇴 처리
        user.setWithdrawn(true);

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            // DB 오류 등 서버 문제만 RuntimeException으로 감싸기
            throw new RuntimeException("회원탈퇴 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}
