package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import com.TeamAA.TeamDo.repository.Project.ProjectRepository;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest // 1. 스프링 부트 서버를 실제로 띄우는 것과 같은 환경을 만듦
@AutoConfigureMockMvc // 2. 가짜 브라우저(MockMvc)를 자동으로 설정
@Transactional // 3. 테스트가 끝나면 DB에 넣었던 데이터를 모두 '롤백(삭제)'해서 깨끗하게 유지함
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // 가짜 브라우저 역할

    @Autowired
    private ObjectMapper objectMapper; // 자바 객체를 JSON으로 바꿔주는 도구

    // 데이터를 미리 넣어야 하므로 리포지토리들도 불러옴
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProjectRepository projectRepository;

    @Test
    @DisplayName("프로젝트 생성 성공 테스트")
    void createProjectSuccess() throws Exception {
        // [Given] 1. 사전 준비 (부모 데이터인 유저와 팀이 있어야 함)
        // (UserEntity, TeamEntity 생성 방식은 본인 코드에 맞게 수정 필요)
        UserEntity user = new UserEntity();
        user.setId("user1");
        user.setEmail("test@test.com");
        user.setName("테스트유저");
        user.setPassword("1234");
        userRepository.save(user);

        TeamEntity team = new TeamEntity();
        team.setName("개발1팀");
        // team.setInviteCode("INV123"); // 필요하면 추가
        team = teamRepository.save(team);

        // 생성할 프로젝트 요청 데이터 (DTO)
        ProjectCreateRequest request = new ProjectCreateRequest();
        // DTO에 Setter가 없다면 리플렉션이나 생성자로 값을 넣어야 할 수도 있음.
        // 여기서는 예시로 JSON 문자열을 직접 만들거나, DTO 필드가 public/setter가 있다고 가정
        // (편의상 여기서는 ObjectMapper가 DTO의 필드에 값을 잘 넣는다고 가정)

        // 실제로는 DTO에 값을 넣는 과정이 필요함 (Setter나 Builder 사용)
        // request.setPname("새로운 프로젝트");
        // request.setTeamId(team.getId());
        // request.setManagerId(user.getId());

        // DTO 대신 Map을 써서 JSON을 만들 수도 있습니다.
        String jsonRequest = """
                {
                    "pname": "새로운 프로젝트",
                    "teamId": %d,
                    "managerId": "%s"
                }
                """.formatted(team.getId(), user.getId());


        // [When] 2. 실제 API 요청 보내기 (POST /api/project/registration)
        mockMvc.perform(post("/api/project/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)) // 위에서 만든 JSON 전송

                // [Then] 3. 결과 검증
                .andExpect(status().isOk()) // 200 OK가 왔는지?
                .andExpect(jsonPath("$.pname").value("새로운 프로젝트")); // 응답의 pname이 맞는지?
    }

    @Test
    @DisplayName("프로젝트 상세 조회 테스트")
    void getProjectSuccess() throws Exception {
        // [Given] 1. 데이터 미리 저장 (유저 -> 팀 -> 프로젝트)
        UserEntity user = new UserEntity();
        user.setId("user2");
        user.setEmail("test2@test.com");
        user.setPassword("1234");
        user.setName("유저2");
        user = userRepository.save(user);

        TeamEntity team = new TeamEntity();
        team.setName("기획팀");
        teamRepository.save(team);

        ProjectEntity project = ProjectEntity.builder()
                .pname("기존 프로젝트")
                .userEntity(user)
                .teamEntity(team)
                .build();
        projectRepository.save(project); // DB에 저장됨 (pno 자동 생성)

        // [When] 2. 조회 API 호출 (GET /api/project/detail/by-pno/{pno})
        mockMvc.perform(get("/api/project/detail/by-pno/" + project.getPno()))

                // [Then] 3. 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pname").value("기존 프로젝트"))
                .andExpect(jsonPath("$.teamName").value("기획팀"));
    }
}