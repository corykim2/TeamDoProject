package com.TeamAA.TeamDo.controller.user;

import com.TeamAA.TeamDo.dto.User.DeleteUserRequest;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class DeleteUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원탈퇴 성공 - 200")
    void deleteUser_success() throws Exception {
        // given
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());
        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                false
        ));
        // MockHttpSession 생성 후 userId 직접 매핑
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", "user123");

        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword("password123");
        //when
        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .sessionAttr("userId", "user123"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 비밀번호 불일치 401")
    void deleteUser_fail_wrongPassword() throws Exception {
        //given
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());
        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                false
        ));
        //세션생성
        MockHttpSession session = new MockHttpSession();
        //세션에 userid매칭
        session.setAttribute("userId", "user123");


        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword("wrongPassword");
        //when
        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다, 올바른 비밀번호를 입력해주세요."));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 세션 없음 401")
    void deleteUser_fail_noSession() throws Exception {
        //given
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());
        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash, "홍길동",
                false
        ));

        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword("password123");


        // 세션에 userId 없음
        //when
        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(new MockHttpSession()))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("로그인 세션이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 비밀번호 미입력 400")
    void deleteUser_fail_validation() throws Exception {
        //given
        // todo:given만들기(완료)
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());
        userRepository.save(new UserEntity(
                "user123",
                "test@example.com",
                hash,
                "홍길동",
                false
        ));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", "user123");

        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword(""); // 공백
        //when
        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."));

    }
}
