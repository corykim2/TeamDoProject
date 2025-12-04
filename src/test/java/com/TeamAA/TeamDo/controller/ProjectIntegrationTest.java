package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.Project.ProjectUpdateRequest;
import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.repository.Project.ProjectRepository;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Team.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;

import com.TeamAA.TeamDo.dto.Project.ProjectCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TeamParticipatingRepository teamParticipatingRepository;

    @Test
    @DisplayName("1. [성공] 프로젝트 생성 테스트")
    void createProject_Success() throws Exception {
        // [Given]
        UserEntity user = userRepository.save(createUser("user1", "테스트유저"));
        TeamEntity team = teamRepository.save(createTeam("개발1팀"));

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setPname("새 프로젝트");
        request.setTeamId(team.getId());
        request.setUserId(user.getId());

        // [When & Then]
        mockMvc.perform(post("/api/project/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pname").value("새 프로젝트"))
                .andExpect(jsonPath("$.teamName").value("개발1팀"));
    }

    @Test
    @DisplayName("1-2. [예외] 존재하지 않는 팀으로 생성 시도")
    void createProject_Fail_InvalidData() throws Exception {
        // [Given] 유저는 있지만 팀은 없는 ID(9999) 사용
        UserEntity user = userRepository.save(createUser("userFail", "실패자"));

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setPname("실패할 프로젝트");
        request.setTeamId(9999L); // 없는 팀 ID
        request.setUserId(user.getId());

        // [When & Then] 400 Bad Request 기대
        mockMvc.perform(post("/api/project/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 예외 발생 확인
    }

    @Test
    @DisplayName("2. [성공] 프로젝트 상세 조회 테스트")
    void getProject_Success() throws Exception {
        // [Given]
        UserEntity user = userRepository.save(createUser("user2", "유저2"));
        TeamEntity team = teamRepository.save(createTeam("기획팀"));
        ProjectEntity project = projectRepository.save(createProject(user, team, "기존 프로젝트"));

        // [When & Then] URL: /api/project/detail/by-pno/{pno}
        mockMvc.perform(get("/api/project/detail/by-pno/" + project.getPno()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pname").value("기존 프로젝트"))
                .andExpect(jsonPath("$.progressPercent").exists());
    }

    @Test
    @DisplayName("2-2. [예외] 존재하지 않는 프로젝트 조회 시 실패")
    void getProject_Fail_NotFound() throws Exception {
        // [When] 없는 ID(9999)로 조회
        mockMvc.perform(get("/api/project/detail/by-pno/9999"))
                // [Then] 400 Bad Request 기대 (GlobalExceptionHandler가 처리)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists()); // 에러 메시지가 있는지 확인
    }

    @Test
    @DisplayName("3. [성공] 프로젝트 수정 테스트 (권한 있음)")
    void updateProject_Success() throws Exception {
        // [Given]
        UserEntity user = userRepository.save(createUser("user3", "팀장"));
        TeamEntity team = teamRepository.save(createTeam("디자인팀"));

        // ★ 팀원 등록 (권한 부여)
        addMemberToTeam(user, team);

        ProjectEntity project = projectRepository.save(createProject(user, team, "수정 전 이름"));

        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest();
        updateRequest.setPname("수정 후 이름");
        updateRequest.setTeamId(team.getId());

        // [When] URL: /api/project/modification/by-pno/{pno}?userId=...
        mockMvc.perform(put("/api/project/modification/by-pno/" + project.getPno())
                        .param("userId", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                // [Then]
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pname").value("수정 후 이름"));
    }

    @Test
    @DisplayName("3-2. [예외] 프로젝트 수정 실패 (권한 없음 - 팀원 아님)")
    void updateProject_Fail_NoPermission() throws Exception {
        // [Given]
        UserEntity owner = userRepository.save(createUser("owner", "원래주인"));
        UserEntity hacker = userRepository.save(createUser("hacker", "해커")); // 팀에 안 들어감
        TeamEntity team = teamRepository.save(createTeam("보안팀"));

        addMemberToTeam(owner, team); // 주인만 팀원 등록
        ProjectEntity project = projectRepository.save(createProject(owner, team, "중요 프로젝트"));

        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest();
        updateRequest.setPname("해킹된 이름");
        updateRequest.setTeamId(team.getId());

        // [When] 해커가 수정 시도
        mockMvc.perform(put("/api/project/modification/by-pno/" + project.getPno())
                        .param("userId", hacker.getId()) // 해커 아이디로 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                // [Then] 400 Bad Request (접근 권한 없음 예외)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. [성공] 프로젝트 삭제 테스트")
    void deleteProject_Success() throws Exception {
        // [Given]
        UserEntity user = userRepository.save(createUser("user4", "삭제자"));
        TeamEntity team = teamRepository.save(createTeam("삭제팀"));
        addMemberToTeam(user, team); // 권한 부여
        ProjectEntity project = projectRepository.save(createProject(user, team, "삭제될 프로젝트"));

        // [When] URL: /api/project/removal/by-pno/{pno}?userId=...
        mockMvc.perform(delete("/api/project/removal/by-pno/" + project.getPno())
                        .param("userId", user.getId()))
                // [Then]
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("4-2. [예외] 프로젝트 삭제 실패 (권한 없음)")
    void deleteProject_Fail_NoPermission() throws Exception {
        // [Given]
        UserEntity owner = userRepository.save(createUser("owner2", "주인2"));
        UserEntity hacker = userRepository.save(createUser("hacker2", "해커2")); // 팀원 아님
        TeamEntity team = teamRepository.save(createTeam("보안팀2"));
        addMemberToTeam(owner, team);
        ProjectEntity project = projectRepository.save(createProject(owner, team, "삭제X"));

        // [When & Then] 해커 ID로 삭제 시도 -> 400 Bad Request
        mockMvc.perform(delete("/api/project/removal/by-pno/" + project.getPno())
                        .param("userId", hacker.getId()))
                .andExpect(status().isBadRequest());
    }

    // --- Helper Methods (데이터 생성 도우미) ---
    private UserEntity createUser(String id, String name) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(id + "@test.com");
        user.setName(name);
        user.setPassword("1234");
        return user;
    }

    private TeamEntity createTeam(String name) {
        TeamEntity team = new TeamEntity();
        team.setName(name);
        return team;
    }

    private ProjectEntity createProject(UserEntity user, TeamEntity team, String pname) {
        return ProjectEntity.builder()
                .pname(pname)
                .userEntity(user)
                .teamEntity(team)
                .build();
    }

    private void addMemberToTeam(UserEntity user, TeamEntity team) {
        TeamParticipatingEntity participating = new TeamParticipatingEntity();
        participating.setUserEntity(user);
        participating.setTeamEntity(team);
        teamParticipatingRepository.save(participating);
    }
}