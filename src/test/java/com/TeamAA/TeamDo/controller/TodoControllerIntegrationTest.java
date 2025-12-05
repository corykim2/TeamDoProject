package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.Interceptor.SessionInterceptor; // ⭐️ 인터셉터 임포트
import com.TeamAA.TeamDo.dto.Todo.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.Todo.TodoStateUpdateRequest;
import com.TeamAA.TeamDo.dto.Todo.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Project.ProjectRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.Todo.TodoRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import com.TeamAA.TeamDo.service.Project.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @MockBean
    private ProjectService projectService;

    // ⭐️ 중요: 인터셉터를 MockBean으로 등록하여 실제 인증 로직을 우회합니다.
    @MockBean
    private SessionInterceptor sessionInterceptor;

    private UserEntity creator;
    private UserEntity manager;
    private ProjectEntity project;
    private TeamEntity team;

    @BeforeEach
    void setUp() throws Exception {
        // 1. 사용자 데이터 세팅
        creator = new UserEntity();
        creator.setId("creatorUser");
        creator.setName("생성자");
        creator.setEmail("creator@test.com");
        creator.setPassword("pw");
        creator.setWithdrawn(false);
        userRepository.save(creator);

        manager = new UserEntity();
        manager.setId("managerUser");
        manager.setName("담당자");
        manager.setEmail("manager@test.com");
        manager.setPassword("pw");
        manager.setWithdrawn(false);
        userRepository.save(manager);

        // 2. 팀 데이터 생성
        team = new TeamEntity();
        team.setName("테스트 팀");
        team.setInviteCode("INVITE123");
        teamRepository.save(team);

        // 3. 프로젝트 데이터 생성
        project = new ProjectEntity();
        project.setPname("테스트 프로젝트");
        project.setTeamEntity(team);
        project.setUserEntity(creator);
        projectRepository.save(project);

        // 4. Mock 설정
        // ProjectService: 프로젝트 멤버 검증 통과
        doNothing().when(projectService).validateUserInProject(anyLong(), anyString());

        // ⭐️ SessionInterceptor: 모든 요청에 대해 true 반환 (인증 통과 처리)
        given(sessionInterceptor.preHandle(any(), any(), any())).willReturn(true);
    }

    // --- 시나리오 1: 할 일 생성 ---
    @Test
    @DisplayName("1-1. 할 일 생성: 정상 처리")
    void createTodo_Success() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setPNo(project.getPno());
        request.setName("API 개발");
        request.setManagerId(manager.getId());
        request.setDeadline(LocalDate.now().plusDays(7));
        request.setPriority(1);

        mockMvc.perform(post("/api/todos/registration")
                        // ⭐️ 인터셉터를 우회했으므로 Controller가 필요로 하는 loginUser 속성을 직접 주입
                        .requestAttr("loginUser", creator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("API 개발"))
                .andExpect(jsonPath("$.state").value("미완"));
    }

    @Test
    @DisplayName("1-2. 할 일 생성: 필수값 누락 (400 Bad Request)")
    void createTodo_MissingFields() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest();
        // 필수 값 누락

        mockMvc.perform(post("/api/todos/registration")
                        .requestAttr("loginUser", creator) // ⭐️ 사용자 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // --- 시나리오 2: 정렬 조회 ---
    @Test
    @DisplayName("2. 정렬 조회: 우선순위 기준 정렬")
    void getTodos_Sorted() throws Exception {
        createTodoEntity("할일1", 1);
        createTodoEntity("할일2", 5);

        mockMvc.perform(get("/api/todos")
                        .requestAttr("loginUser", creator) // ⭐️ 사용자 주입
                        .param("sortBy", "priority")
                        .param("direction", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[1].priority").value(5));
    }

    // --- 시나리오 3: 프로젝트 TODO 전체 조회 ---
    @Test
    @DisplayName("3. 프로젝트별 할 일 목록 조회: 정상 처리")
    void getTodosByProject_Success() throws Exception {
        createTodoEntity("프로젝트 할일", 1);

        mockMvc.perform(get("/api/todos-list/by-project/{pNo}", project.getPno())
                        .requestAttr("loginUser", creator)) // ⭐️ 사용자 주입
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("프로젝트 할일"));
    }

    // --- 시나리오 4: 할 일 수정 (내용) ---
    @Test
    @DisplayName("4. 할 일 수정: 정상 처리")
    void updateTodo_Success() throws Exception {
        TodoEntity todo = createTodoEntity("수정 전 이름", 1);
        TodoUpdateRequest updateReq = new TodoUpdateRequest();
        updateReq.setName("수정 후 이름");
        updateReq.setPriority(3);

        mockMvc.perform(patch("/api/todos/modification/by-todoId/{todoId}", todo.getTodoId())
                        .requestAttr("loginUser", creator) // ⭐️ 생성자 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정 후 이름"));
    }

    // --- 시나리오 5: 할 일 완료 상태 업데이트 ---
    @Test
    @DisplayName("5. 할 일 상태 업데이트: 정상 처리")
    void updateTodoState_Success() throws Exception {
        TodoEntity todo = createTodoEntity("상태 변경 테스트", 1);
        TodoStateUpdateRequest stateReq = new TodoStateUpdateRequest();
        stateReq.setState("DONE");

        mockMvc.perform(put("/api/todos/modification-state/by-todoId/{todoId}/state", todo.getTodoId())
                        .requestAttr("loginUser", manager) // ⭐️ 담당자(Manager) 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("DONE"));
    }

    // --- 시나리오 6: 할 일 삭제 ---
    @Test
    @DisplayName("6. 할 일 삭제: 정상 처리")
    void deleteTodo_Success() throws Exception {
        TodoEntity todo = createTodoEntity("삭제할 일", 1);

        mockMvc.perform(delete("/api/todos/removal/by-todoId/{todoId}", todo.getTodoId())
                        .requestAttr("loginUser", creator)) // ⭐️ 생성자 주입
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(todoRepository.findById(todo.getTodoId()).isEmpty());
    }
    // 1-2. 할 일 생성 실패: 필수값 누락 (400 Bad Request)
    // (이미 작성하신 createTodo_MissingFields가 있지만, 시나리오 완성을 위해 포함합니다)
    @Test
    @DisplayName("1-2. 할 일 생성 실패: 필수값 누락")
    void createTodo_Failure_MissingFields() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest();
        // 필수 값 누락 상태

        mockMvc.perform(post("/api/todos/registration")
                        .requestAttr("loginUser", creator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        // GlobalExceptionHandler 설정에 따라 .andExpect(jsonPath("$.name").exists()) 등을 추가할 수 있음
    }

    // 2-2. 특정 할 일 조회 실패: 존재하지 않는 ID (404 Not Found)
    @Test
    @DisplayName("2-2. 특정 할 일 조회 실패: 존재하지 않는 ID")
    void getTodo_Failure_NotFound() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(get("/api/todos/details/by-todoId/{todoId}", nonExistentId)
                        .requestAttr("loginUser", creator))
                .andDo(print())
                .andExpect(status().isNotFound()); // 404 확인
    }

    // 3-2. 프로젝트별 조회 실패: 존재하지 않는 프로젝트 (404 Not Found)
    @Test
    @DisplayName("3-2. 프로젝트별 조회 실패: 존재하지 않는 프로젝트")
    void getTodosByProject_Failure_NotFound() throws Exception {
        Long nonExistentPno = 99999L;

        // Mock 설정: 존재하지 않는 프로젝트 ID로 검증 시 예외 발생 유도
        // (Service 로직에서 validateUserInProject가 호출될 때 예외를 던지도록 설정)
        doThrow(new jakarta.persistence.EntityNotFoundException("프로젝트를 찾을 수 없습니다."))
                .when(projectService).validateUserInProject(anyLong(), anyString());

        mockMvc.perform(get("/api/todos-list/by-project/{pNo}", nonExistentPno)
                        .requestAttr("loginUser", creator))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // 4-2. 할 일 수정 실패: 존재하지 않는 ID (404 Not Found)
    @Test
    @DisplayName("4-2. 할 일 수정 실패: 존재하지 않는 ID")
    void updateTodo_Failure_NotFound() throws Exception {
        Long nonExistentId = 99999L;
        TodoUpdateRequest updateReq = new TodoUpdateRequest();
        updateReq.setName("수정 시도");

        mockMvc.perform(patch("/api/todos/modification/by-todoId/{todoId}", nonExistentId)
                        .requestAttr("loginUser", creator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // 4-3. 할 일 수정 실패: 권한 없음 (403 Forbidden)
    // 생성자가 아닌 사람(Manager)이 수정을 시도할 경우
    @Test
    @DisplayName("4-3. 할 일 수정 실패: 권한 없음 (생성자가 아님)")
    void updateTodo_Failure_Forbidden() throws Exception {
        TodoEntity todo = createTodoEntity("권한 테스트", 1);
        TodoUpdateRequest updateReq = new TodoUpdateRequest();
        updateReq.setName("불법 수정 시도");

        // creator가 만든 todo를 manager가 수정하려고 시도
        mockMvc.perform(patch("/api/todos/modification/by-todoId/{todoId}", todo.getTodoId())
                        .requestAttr("loginUser", manager) // ⭐️ 다른 유저 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isForbidden()); // 403 확인
    }

    // 5-2. 상태 업데이트 실패: 존재하지 않는 ID (404 Not Found)
    @Test
    @DisplayName("5-2. 상태 업데이트 실패: 존재하지 않는 ID")
    void updateTodoState_Failure_NotFound() throws Exception {
        Long nonExistentId = 99999L;
        TodoStateUpdateRequest stateReq = new TodoStateUpdateRequest();
        stateReq.setState("DONE");

        mockMvc.perform(put("/api/todos/modification-state/by-todoId/{todoId}/state", nonExistentId)
                        .requestAttr("loginUser", manager)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateReq)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // 5-3. 상태 업데이트 실패: 권한 없음 (403 Forbidden)
    // 담당자가 아닌 사람(Creator)이 상태 변경을 시도할 경우
    @Test
    @DisplayName("5-3. 상태 업데이트 실패: 권한 없음 (담당자가 아님)")
    void updateTodoState_Failure_Forbidden() throws Exception {
        TodoEntity todo = createTodoEntity("상태 권한 테스트", 1);
        TodoStateUpdateRequest stateReq = new TodoStateUpdateRequest();
        stateReq.setState("DONE");

        // manager가 담당자인 todo를 creator가 상태 변경하려고 시도
        mockMvc.perform(put("/api/todos/modification-state/by-todoId/{todoId}/state", todo.getTodoId())
                        .requestAttr("loginUser", creator) // ⭐️ 담당자가 아닌 유저 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateReq)))
                .andDo(print())
                .andExpect(status().isForbidden()); // 403 확인
    }

    // 6-2. 할 일 삭제 실패: 존재하지 않는 ID (404 Not Found)
    @Test
    @DisplayName("6-2. 할 일 삭제 실패: 존재하지 않는 ID")
    void deleteTodo_Failure_NotFound() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(delete("/api/todos/removal/by-todoId/{todoId}", nonExistentId)
                        .requestAttr("loginUser", creator))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // 6-3. 할 일 삭제 실패: 권한 없음 (403 Forbidden)
    // 생성자가 아닌 사람(Manager)이 삭제를 시도할 경우
    @Test
    @DisplayName("6-3. 할 일 삭제 실패: 권한 없음 (생성자가 아님)")
    void deleteTodo_Failure_Forbidden() throws Exception {
        TodoEntity todo = createTodoEntity("삭제 권한 테스트", 1);

        // creator가 만든 todo를 manager가 삭제하려고 시도
        mockMvc.perform(delete("/api/todos/removal/by-todoId/{todoId}", todo.getTodoId())
                        .requestAttr("loginUser", manager) // ⭐️ 다른 유저 주입
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden()); // 403 확인
    }

    // Helper Method
    private TodoEntity createTodoEntity(String name, Integer priority) {
        TodoEntity todo = new TodoEntity();
        todo.setName(name);
        todo.setProjectEntity(project);
        todo.setCreatorId(creator);
        todo.setManagerId(manager);
        todo.setDeadline(LocalDate.now());
        todo.setPriority(priority);
        todo.setState("미완");
        return todoRepository.save(todo);
    }
}