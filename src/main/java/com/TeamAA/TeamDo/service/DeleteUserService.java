package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.DeleteUserRequest;
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
    public UserEntity deleteUser(String Id, DeleteUserRequest request) {

        try {// 비밀번호 입력 누락 예외 처리
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("비밀번호를 입력해주세요");
            }
            // 사용자 조회
            UserEntity user = userRepository.findById(Id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            // 비밀번호 불일치 예외 처리
            if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다, 올바른 비밀번호를 입력해주세요.");
            }

            // 탈퇴 처리
            user.setWithdrawn(true);
            userRepository.save(user);

            return user;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("회원탈퇴 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}
