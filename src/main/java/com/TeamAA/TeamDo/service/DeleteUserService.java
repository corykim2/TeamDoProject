package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.DeleteUserRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.UserRepository;
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
        UserEntity user = userRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        // 해싱 비밀번호 검증
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.setWithdrawn(true);
        userRepository.save(user);
        return user;
    }
}
