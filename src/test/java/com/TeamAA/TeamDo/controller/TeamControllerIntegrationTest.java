package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Team.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ImportAutoConfiguration(
        exclude = {
                org.springframework.boot.autoconfigure.session.SessionAutoConfiguration.class
        }
)
@Transactional
@ActiveProfiles("test")
class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamParticipatingRepository teamParticipatingRepository;

    // ================= Helper =================

    private MockHttpSession loginSession(String userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        return session;
    }

    private UserEntity createUser(String id, String name) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(id + "@test.com");
        user.setName(name);
        user.setPassword("1234");
        return userRepository.save(user);
    }

    private TeamEntity createTeamEntity(String name) {
        TeamEntity team = new TeamEntity();
        team.setName(name);
        return teamRepository.save(team);
    }

    private TeamResponse createTeamViaService(String name, UserEntity leader) {
        // 팀 엔티티 생성
        TeamEntity team = createTeamEntity(name);

        // 팀장 참여 등록
        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setTeamEntity(team);
        participation.setUserEntity(leader);
        teamParticipatingRepository.save(participation);

        // TeamResponse DTO 반환
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getInviteCode(),
                List.of() // 테스트용이므로 빈 리스트
        );
    }

    // ================= Tests =================

    @Test
    @DisplayName("1. 팀 생성 성공")
    void createTeamSuccess() throws Exception {
        UserEntity leader = createUser("leader1", "팀장");

        MockHttpSession session = loginSession(leader.getId());
        mockMvc.perform(post("/api/teams")
                        .param("name", "새로운팀")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("새로운팀")));
    }

    @Test
    @DisplayName("2. 내 팀 목록 조회 성공")
    void getMyTeamsSuccess() throws Exception {
        UserEntity leader = createUser("leader2", "팀장2");
        createTeamViaService("테스트팀", leader);

        mockMvc.perform(get("/api/teams")
                        .session(loginSession(leader.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트팀"));
    }

    @Test
    @DisplayName("3. 팀 상세 조회 성공")
    void getTeamDetailSuccess() throws Exception {
        // 1. 팀장 유저 생성
        UserEntity leader = createUser("leader3", "팀장3");

        // 2. 팀 생성 및 팀장 참여 등록
        TeamResponse team = createTeamViaService("테스트팀3", leader);

        // 3. 세션 생성
        MockHttpSession session = loginSession(leader.getId());

        // 4. 요청 수행
        mockMvc.perform(get("/api/teams/" + team.id())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트팀3"))
                .andExpect(jsonPath("$.inviteCode").value(team.inviteCode()));
    }

    @Test
    @DisplayName("4. 초대코드로 팀 참가 성공")
    void joinTeamByInviteCodeSuccess() throws Exception {
        UserEntity leader = createUser("leader4", "팀장4");
        UserEntity member = createUser("member1", "팀원1");
        TeamResponse team = createTeamViaService("테스트팀4", leader);

        mockMvc.perform(post("/api/teams/join/by-invite-code")
                        .param("inviteCode", team.inviteCode())
                        .session(loginSession(member.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("팀 참가 완료"));
    }

    @Test
    @DisplayName("5. 팀 나가기 성공")
    void leaveTeamSuccess() throws Exception {
        UserEntity leader = createUser("leader5", "팀장5");
        UserEntity member = createUser("member5", "팀원5");
        TeamResponse team = createTeamViaService("테스트팀5", leader);

        // 팀원 참여 등록
        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setTeamEntity(teamRepository.getReferenceById(team.id()));
        participation.setUserEntity(member);
        teamParticipatingRepository.save(participation);

        mockMvc.perform(delete("/api/teams/" + team.id() + "/leave")
                        .session(loginSession(member.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("팀에서 나갔습니다."));
    }

    @Test
    @DisplayName("6. 초대코드 재발급 성공 (팀장)")
    void regenerateInviteCodeSuccess() throws Exception {
        UserEntity leader = createUser("leader6", "팀장6");
        TeamResponse team = createTeamViaService("테스트팀6", leader);

        mockMvc.perform(put("/api/teams/" + team.id() + "/invite-code")
                        .session(loginSession(leader.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("새 초대코드")));
    }
}
