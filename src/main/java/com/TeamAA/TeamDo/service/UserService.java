package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 회원가입
    public UserEntity signup(SignupRequest request) {
        if (userRepository.findById(request.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        UserEntity user = new UserEntity();
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 해싱은 나중에
        user.setName(request.getName());
        user.setWithdrawn(false);

        return userRepository.save(user);
    }

    // 로그인용 유저 조회
    public UserEntity findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}