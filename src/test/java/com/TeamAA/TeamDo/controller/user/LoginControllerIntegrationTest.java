package com.TeamAA.TeamDo.controller.user;

import com.TeamAA.TeamDo.dto.User.LoginRequest;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 - 200")
    void login_success() throws Exception {

        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());

        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                false
        ));

        LoginRequest request = new LoginRequest();
        request.setId("user123");
        request.setPassword("password123");

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인완료"));
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 없음 (401)")
    void login_fail_idNotFound() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setId("unknownUser");
        request.setPassword("password123");

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못 되었습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 (401)")
    void login_fail_wrongPassword() throws Exception {

        String hash = BCrypt.hashpw("correctPw", BCrypt.gensalt());

        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                false
        ));

        LoginRequest request = new LoginRequest();
        request.setId("user123");
        request.setPassword("wrongPw");

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못 되었습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴 계정 (403)")
    void login_fail_withdrawnUser() throws Exception {

        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());

        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                true
        ));

        LoginRequest request = new LoginRequest();
        request.setId("user123");
        request.setPassword("password123");

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("탈퇴한 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 요청값 검증 실패 (400)")
    void login_fail_validation() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setId("");
        request.setPassword("");

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}