package com.TeamAA.TeamDo.controller.user;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.dto.User.LogoutResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LogoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("userId", "user123"); // 실제 로그인된 세션처럼 세팅
    }

    @Test
    @DisplayName("로그아웃 성공 - 200")
    void logout_success() throws Exception {
        mockMvc.perform(delete("/sessions")
                        .session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 실패 - 세션 없음 401")
    void logout_fail_noSession() throws Exception {
        MockHttpSession emptySession = new MockHttpSession(); // userId 없음

        mockMvc.perform(delete("/sessions")
                        .session(emptySession))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("세션이 만료되었습니다."));
    }
}