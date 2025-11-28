package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.Todo.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.Todo.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Todo.TodoRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ⚠️ ProjectEntity 정의가 없으므로 테스트를 위해 주입 필요
    // @Autowired
    // private ProjectRepository projectRepository;

    private UserEntity creatorUser;
    private UserEntity anotherUser;
    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 사용자 생성 (ID는 String 타입)
        creatorUser = new UserEntity();
        creatorUser.setId("creator1"); // ⭐️ String ID 사용
        creatorUser.setEmail("creator1@test.com");
        creatorUser.setPassword("hashed_password");
        creatorUser.setName("Creator Name");
        userRepository.save(creatorUser);

        anotherUser = new UserEntity();
        anotherUser.setId("user2"); // ⭐️ String ID 사용
        anotherUser.setEmail("user2@test.com");
        anotherUser.setPassword("hashed_password2");
        anotherUser.setName("Another Name");
        userRepository.save(anotherUser);

        // 2. 테스트용 프로젝트 생성 (ProjectEntity에 setPno가 있다고 가정)
        testProject = new ProjectEntity();
        testProject.setPno(1L);
        // projectRepository.save(testProject); // 실제 DB에 저장 필요
    }

    @Test
    @DisplayName("1. 할 일 생성: 성공 (POST /api/todos)")
    void createTodo_Success() throws Exception {
        TodoCreateRequest requestDto = new TodoCreateRequest();
        requestDto.setName("테스트 할 일");
        requestDto.setManagerId(creatorUser.getId()); // ⭐️ String ID 사용
        requestDto.setPNo(1L);
        requestDto.setDeadline(LocalDate.now().plusDays(1));
        requestDto.setPriority(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .requestAttr("loginUser", creatorUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트 할 일"))
                .andExpect(jsonPath("$.creatorId.id").value(creatorUser.getId())) // ⭐️ String ID 비교
                .andExpect(jsonPath("$.state").value("미완"));
    }

    @Test
    @DisplayName("2. 특정 할 일 조회: 성공 (GET /api/todos/{todoId})")
    void getTodo_Success() throws Exception {
        TodoEntity todo = createTestTodo("조회 테스트", creatorUser, anotherUser);
        Long todoId = todo.getTodoId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todos/{todoId}", todoId)
                        .requestAttr("loginUser", creatorUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("조회 테스트"))
                .andExpect(jsonPath("$.todoId").value(todoId));
    }

    @Test
    @DisplayName("3. 할 일 수정: 생성자 권한 없음 (PATCH /api/todos/{todoId})")
    void updateTodo_Forbidden() throws Exception {
        TodoEntity todo = createTestTodo("수정 테스트", creatorUser, creatorUser);
        Long todoId = todo.getTodoId();

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setName("새로운 이름");

        // MockMvc 요청: 다른 사용자 (anotherUser)가 수정 시도
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/todos/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .requestAttr("loginUser", anotherUser))
                .andExpect(status().isForbidden()); // 403 Forbidden 기대
    }

    @Test
    @DisplayName("4. 할 일 삭제: 성공 (DELETE /api/todos/{todoId})")
    void deleteTodo_Success() throws Exception {
        TodoEntity todo = createTestTodo("삭제 테스트", creatorUser, creatorUser);
        Long todoId = todo.getTodoId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todos/{todoId}", todoId)
                        .requestAttr("loginUser", creatorUser))
                .andExpect(status().isOk());

        Optional<TodoEntity> deletedTodo = todoRepository.findById(todoId);
        assertTrue(deletedTodo.isEmpty());
    }

    /**
     * 테스트용 TodoEntity 생성 헬퍼 메서드
     */
    private TodoEntity createTestTodo(String name, UserEntity creator, UserEntity manager) {
        TodoEntity todo = new TodoEntity();
        todo.setName(name);
        todo.setProjectEntity(testProject); // ProjectEntity 설정
        todo.setCreatorId(creator);
        todo.setManagerId(manager);
        todo.setDeadline(LocalDate.now().plusDays(1));
        todo.setPriority(1);
        todo.setState("미완");
        return todoRepository.save(todo);
    }
}