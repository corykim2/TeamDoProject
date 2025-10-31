package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.User;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //Mock객체 초기화
    }

    @Test
    @DisplayName("테스트 실행")
    void findById_존재하는아이디() {

        // given: 유저를 Mock 설정
            User user = new User();
            user.setId("test");
            when(userRepository.findById("test")).thenReturn(Optional.of(user));


        // when: findById() 실행
            User result = userService.findById("test");

        // then: findById() 검증
            assertNotNull(result);// null 아님 확인
            assertEquals("test", result.getId());
    }

    @Test
    @DisplayName("존재하지 않는 아이디 테스트 실행")
    void findById_존재하지않는아이디() {
        // given
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        // when
        User result = userService.findById("unknown");

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("로그인: 아이디 확인 후 비밀번호 일치 검증")
    void login_아이디확인후비밀번호일치() {
        // given
        User user = new User();
        user.setId("test");
        user.setPassword("1234");
        when(userRepository.findById("test")).thenReturn(Optional.of(user));

        // when
        User result = userService.login("test", "1234"); //로그인 호출

        // then
        assertNotNull(result);
        assertEquals("test", result.getPassword()); // 비밀번호 일치 확인
    }
}