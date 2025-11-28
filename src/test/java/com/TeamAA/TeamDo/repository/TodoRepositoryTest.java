package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.ProjectEntity;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest // JPA 관련 설정만 로드하여 테스트
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ProjectRepository projectRepository; // 프로젝트 생성을 위해 주입
    @Autowired
    private UserRepository userRepository; // 사용자 생성을 위해 주입

    private ProjectEntity project1;
    private ProjectEntity project2;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        // 1. 테스트 사용자 설정 (ID: String)
        user = new UserEntity();
        user.setId("testUser");
        user.setEmail("test@test.com");
        user.setPassword("hashedpass");
        user.setName("Test User");
        userRepository.save(user);

        // 2. 테스트 프로젝트 설정
        project1 = new ProjectEntity();
        project1.setPno(100);
        project1.setPname("Project Alpha");
        projectRepository.save(project1);

        project2 = new ProjectEntity();
        project2.setPno(200);
        project2.setPname("Project Beta");
        projectRepository.save(project2);

        // 3. 테스트 Todo 데이터 저장
        // Project 100: 미완, 완료, 보류 (3개)
        saveTodo("Alpha 미완", project1, "미완");
        saveTodo("Alpha 완료", project1, "완료");
        saveTodo("Alpha 보류", project1, "보류");

        // Project 200: 미완 (1개)
        saveTodo("Beta 미완", project2, "미완");

        todoRepository.flush(); // DB에 즉시 반영
    }

    private void saveTodo(String name, ProjectEntity project, String state) {
        TodoEntity todo = new TodoEntity();
        todo.setName(name);
        todo.setProjectEntity(project);
        todo.setCreatorId(user);
        todo.setManagerId(user);
        todo.setDeadline(LocalDate.now());
        todo.setPriority(1);
        todo.setState(state);
        todoRepository.save(todo);
    }

    // --- 테스트 시작 ---

    @Test
    @DisplayName("1. findByProjectEntity_pno: 프로젝트별 Todo 조회 성공")
    void findByProjectEntity_pno_Success() {
        // When
        List<TodoEntity> todosAlpha = todoRepository.findByProjectEntity_pno(100);
        List<TodoEntity> todosBeta = todoRepository.findByProjectEntity_pno(200);

        // Then
        assertEquals(3, todosAlpha.size());
        assertEquals(1, todosBeta.size());
        assertTrue(todosAlpha.stream().anyMatch(t -> t.getName().contains("Alpha 완료")));
    }

    // ⭐️ TodoRepository에 이 쿼리 메서드를 추가해야 합니다!
    // @Test
    @DisplayName("2. findByProjectEntity_pnoAndStateNot: 완료 제외 필터링 조회 성공")
    void findByProjectEntity_pnoAndStateNot_Success() {
        // ⚠️ 테스트를 위해 TodoRepository에 다음 메서드 정의가 필수:
        // List<TodoEntity> findByProjectEntity_pnoAndStateNot(Integer pNo, String state);

        // When
        // Project 100에서 '완료' 상태가 아닌 것만 조회 (기대값: 미완, 보류 => 2개)
        List<TodoEntity> activeTodosAlpha = todoRepository.findByProjectEntity_pnoAndStateNot(100, "완료");

        // Then
        assertEquals(2, activeTodosAlpha.size());
        assertTrue(activeTodosAlpha.stream().noneMatch(t -> t.getState().equals("완료")));
        assertTrue(activeTodosAlpha.stream().anyMatch(t -> t.getState().equals("보류")));
        assertTrue(activeTodosAlpha.stream().anyMatch(t -> t.getState().equals("미완")));
    }
}