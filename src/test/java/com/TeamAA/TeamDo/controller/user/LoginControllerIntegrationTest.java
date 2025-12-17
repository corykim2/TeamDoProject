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
        //given
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
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인완료"));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지않는 아이디 (401)")
    void login_fail_idNotFound() throws Exception {

        //given 없이 테스트 진행

        LoginRequest request = new LoginRequest();
        request.setId("unknownUser");
        request.setPassword("password123");
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못 되었습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 (401)")
    void login_fail_wrongPassword() throws Exception {
        //given
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
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못 되었습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴 계정 (403)")
    void login_fail_withdrawnUser() throws Exception {
        //given
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
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("탈퇴한 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 요청값 검증 실패 - 아이디 공백입력 (400)")
    void login_fail_blank_id() throws Exception {
        //given 없이 진행

        LoginRequest request = new LoginRequest();
        request.setId(""); //공백입력 체크
        request.setPassword("password123");
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
        ;

    }

    @Test
    @DisplayName("로그인 실패 - 요청값 검증 실패 - 비밀번호 공백입력 (400)")
    void login_fail_blank_pass() throws Exception {
        //given 없이 진행

        LoginRequest request = new LoginRequest();
        request.setId("tset1234");
        request.setPassword(""); //비밀번호 공백입력
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."));
        ;
    }

    @Test
    @DisplayName("로그인 실패 - 요청값 검증 실패 - 아이디 사이즈초과 (400)")
    void login_fail_size_id() throws Exception {
        //given 없이 진행

        LoginRequest request = new LoginRequest();
        request.setId("tset1234tset1234tset1234tset1234tset1234tset1234tset1234tset1234tset1234"); //비정상적인 아이디길이 기입
        request.setPassword("password1234"); //비밀번호 공백입력
        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("올바른 아이디를 입력해주세요."));
    }

    @Test
    @DisplayName("로그인 실패 - 요청값 검증 실패 - 비밀번호 사이즈초과 (400)")
    void login_fail_size_pass() throws Exception {
        //given 없이 진행

        LoginRequest request = new LoginRequest();
        request.setId("tset123");
        request.setPassword("password1234passworord1234password1234password1234password1234"); //비정상적인 비밀번호 입력

        //when
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("올바른 비밀번호를 입력해주세요."));
    }
}