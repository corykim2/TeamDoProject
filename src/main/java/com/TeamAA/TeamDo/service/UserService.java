package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.User;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(User user) {
        if(userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}