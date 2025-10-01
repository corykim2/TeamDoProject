package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.User;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 신규 회원가입
    public User signUp(User user) {

        //아이디 중복 확인
        if(userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 확인
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        //비밀번호 암호화 추가 해야함

        return userRepository.save(user); // DB에 저장
    }

    // 아이디로 회원 조회
    public User findById(String id) {
        return userRepository.findById(id)
                .orElse(null);
    }
}