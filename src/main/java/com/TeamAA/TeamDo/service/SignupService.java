package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

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

        // 비밀번호 해싱
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

        UserEntity user = new UserEntity();
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword); // 해싱된 비밀번호 저장
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