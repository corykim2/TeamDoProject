package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.User;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(">>> 로그인 시도: " + username); // 로그 찍기

        User user = userRepository.findById(username)
                .orElseThrow(() -> {
                    System.out.println(">>> 사용자를 찾을 수 없습니다: " + username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });

        System.out.println(">>> 사용자 조회 성공: " + user.getId());

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
