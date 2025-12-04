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
@AutoConfigureMockMvc(addFilters = false)
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
        //given
        //세션 생성후 id매핑
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", "user123");
        //when
        mockMvc.perform(delete("/sessions")
                        .session(session))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));
    }

    @Test
    @DisplayName("로그아웃 실패 - 세션 없음 401")
    void logout_fail_noSession() throws Exception {
        //given
        MockHttpSession emptySession = new MockHttpSession(); // userId 없음
        //when
        mockMvc.perform(delete("/sessions")
                        .session(emptySession))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("로그인 세션이 유효하지 않습니다."));
    }
}