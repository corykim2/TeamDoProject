package com.TeamAA.TeamDo.controller.user;

import com.TeamAA.TeamDo.dto.User.SignupRequest;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공 - 200")
    void signupSuccess() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setId("user123");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("홍길동");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("아이디 중복 - 409")
    void signupFail_duplicateId() throws Exception {

        userRepository.save(new UserEntity(
                "user123", "test@example.com", "pw", "홍길동", false
        ));

        SignupRequest request = new SignupRequest();
        request.setId("user123");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("새이름");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디입니다."));
    }

    @Test
    @DisplayName("이메일 중복 - 409")
    void signupFail_duplicateEmail() throws Exception {

        userRepository.save(new UserEntity(
                "userAAA", "test@example.com", "pw", "홍길동", false
        ));

        SignupRequest request = new SignupRequest();
        request.setId("user123");
        request.setEmail("test@example.com"); // 기존 이메일
        request.setPassword("password123");
        request.setName("홍길동");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));
    }

    @Test
    @DisplayName("탈퇴한 사용자 재가입 시도 - 403")
    void signupFail_withdrawnUser() throws Exception {

        userRepository.save(new UserEntity(
                "withdrawUser", "withdraw@example.com", "pw", "탈퇴유저", true
        ));

        SignupRequest request = new SignupRequest();
        request.setId("withdrawUser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setName("새이름");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("탈퇴한 사용자 입니다. 탈퇴한 사용자는 재가입이 불가능합니다."));
    }

    @Test
    @DisplayName("아이디 누락 - 400 Bad Request")
    void signupFail_blank_id() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setId("");
        request.setEmail("test@exam.com");
        request.setPassword("password1234");
        request.setName("testuser");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.message").value("아이디를 입력해주세요."))
        ;
    }

    @Test
    @DisplayName("이메일 누락 → 400 Bad Request")
    void signupFail_blank_email() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setId("test1234");
        request.setEmail("");
        request.setPassword("password1234");
        request.setName("testuser");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.message").value("이메일을 입력해주세요."))
        ;
    }

    @Test
    @DisplayName("비밀번호 누락 → 400 Bad Request")
    void signupFail_blank_password() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setId("test1234");
        request.setEmail("test@exam.com");
        request.setPassword(""); //비밀번호 누락
        request.setName("testuser");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."))
        ;
    }

    @Test
    @DisplayName("이름 누락 → 400 Bad Request")
    void signupFail_blank_name() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setId("test1234");
        request.setEmail("test@exam.com");
        request.setPassword("password1234");
        request.setName(""); //이름 누락

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.message").value("이름을 입력해주세요."))
        ;
    }
}
