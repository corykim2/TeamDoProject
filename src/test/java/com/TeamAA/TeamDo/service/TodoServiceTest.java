package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.Todo.TodoCreateRequest;
import com.TeamAA.TeamDo.entity.ProjectEntity;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.ProjectRepository;
import com.TeamAA.TeamDo.repository.TodoRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
import com.TeamAA.TeamDo.service.Todo.TodoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용 선언
public class TodoServiceTest {

    @InjectMocks // 테스트 대상인 TodoService에 Mock 객체들을 주입
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;

    private UserEntity creatorUser;
    private UserEntity managerUser;
    private UserEntity unauthorizedUser;
    private TodoEntity testTodo;
    private ProjectEntity testProject;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 사용자 설정 (ID: String)
        creatorUser = new UserEntity();
        creatorUser.setId("creator1");

        managerUser = new UserEntity();
        managerUser.setId("manager1");

        unauthorizedUser = new UserEntity();
        unauthorizedUser.setId("user99");

        // 2. 테스트용 프로젝트 설정
        testProject = new ProjectEntity();
        testProject.setPno(99);

        // 3. 테스트용 Todo 설정
        testTodo = new TodoEntity();
        testTodo.setTodoId(1L);
        testTodo.setName("원래 할 일 이름");
        testTodo.setProjectEntity(testProject);
        testTodo.setCreatorId(creatorUser);
        testTodo.setManagerId(managerUser);
        testTodo.setState("미완");
        testTodo.setDeadline(LocalDate.now());
    }

    // -----------------------------------------------------
    // 1. 할 일 생성 테스트
    // -----------------------------------------------------

    @Test
    @DisplayName("1.1. 할 일 생성 성공: 상태 미완, 생성자 및 담당자 할당 확인")
    void createTodo_Success() {
        // Given
        TodoCreateRequest requestDto = new TodoCreateRequest();
        requestDto.setName("새로운 할 일");
        requestDto.setManagerId(managerUser.getId());
        requestDto.setPNo(99);
        requestDto.setDeadline(LocalDate.now());
        requestDto.setPriority(1);

        // Mocking: Repository 호출 설정
        when(projectRepository.findByPno(anyInt())).thenReturn(testProject);
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(testTodo);

        // When
        TodoEntity result = todoService.createTodo(requestDto, creatorUser);

        // Then
        assertNotNull(result);
        assertEquals("새로운 할 일", result.getName());
        assertEquals("미완", result.getState()); // 기본 상태 확인
        assertEquals(creatorUser.getId(), result.getCreatorId().getId());
        assertEquals(managerUser.getId(), result.getManagerId().getId());

        // Verify: save 메서드가 한 번 호출되었는지 확인
        verify(todoRepository, times(1)).save(any(TodoEntity.class));
    }

    @Test
    @DisplayName("1.2. 할 일 생성 실패: 담당자 미존재")
    void createTodo_Failure_ManagerNotFound() {
        // Given
        TodoCreateRequest requestDto = new TodoCreateRequest();
        requestDto.setManagerId("nonExistentId");

        // Mocking: 담당자 ID로 조회 시 비어있는 Optional 반환 설정
        when(userRepository.findById("nonExistentId")).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            todoService.createTodo(requestDto, creatorUser);
        });

        // Verify: save 메서드는 호출되지 않았는지 확인
        verify(todoRepository, never()).save(any(TodoEntity.class));
    }

    // -----------------------------------------------------
    // 2. 권한 확인 (인가) 테스트
    // -----------------------------------------------------

    @Test
    @DisplayName("2.1. 할 일 삭제 성공: 생성자 권한 확인")
    void deleteTodo_Success_ByCreator() throws IllegalAccessException {
        // Mocking: Todo 조회 성공 설정
        when(todoRepository.findById(testTodo.getTodoId())).thenReturn(Optional.of(testTodo));

        // When
        todoService.deleteTodo(testTodo.getTodoId(), creatorUser);

        // Then
        // verify(todoRepository, times(1)).delete(any(TodoEntity.class)); // deleteById를 호출해도 무방
        verify(todoRepository, times(1)).delete(testTodo);
    }

    @Test
    @DisplayName("2.2. 할 일 삭제 실패: 권한 없음 (생성자 아님)")
    void deleteTodo_Failure_Unauthorized() {
        // Mocking: Todo 조회 성공 설정
        when(todoRepository.findById(testTodo.getTodoId())).thenReturn(Optional.of(testTodo));

        // Then
        assertThrows(IllegalAccessException.class, () -> {
            todoService.deleteTodo(testTodo.getTodoId(), unauthorizedUser);
        });

        // Verify: 삭제 메서드는 호출되지 않았는지 확인
        verify(todoRepository, never()).delete(any(TodoEntity.class));
    }

    @Test
    @DisplayName("2.3. 상태 업데이트 성공: 담당자 권한 확인")
    void updateTodoState_Success_ByManager() throws IllegalAccessException {
        // Mocking: Todo 조회 성공 설정
        when(todoRepository.findById(testTodo.getTodoId())).thenReturn(Optional.of(testTodo));

        // When
        TodoEntity result = todoService.updateTodoState(testTodo.getTodoId(), "진행중", managerUser);

        // Then
        assertEquals("진행중", result.getState());
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    @DisplayName("2.4. 상태 업데이트 실패: 권한 없음 (담당자 아님)")
    void updateTodoState_Failure_Unauthorized() {
        // Mocking: Todo 조회 성공 설정
        when(todoRepository.findById(testTodo.getTodoId())).thenReturn(Optional.of(testTodo));

        // Then
        assertThrows(IllegalAccessException.class, () -> {
            todoService.updateTodoState(testTodo.getTodoId(), "진행중", unauthorizedUser);
        });
    }

    // -----------------------------------------------------
    // 3. 데이터 필터링 테스트 (getTodosByProjectEntity)
    // -----------------------------------------------------

    @Test
    @DisplayName("3.1. 프로젝트 할 일 조회: 완료된 할 일 제외 필터링 확인")
    void getTodosByProjectEntity_FiltersCompleted() {
        // Given (테스트를 위해 TodoEntity 리스트 생성)
        TodoEntity completeTodo = new TodoEntity();
        completeTodo.setTodoId(2L);
        completeTodo.setState("완료");
        completeTodo.setProjectEntity(testProject);

        TodoEntity incompleteTodo1 = new TodoEntity();
        incompleteTodo1.setTodoId(3L);
        incompleteTodo1.setState("미완");
        incompleteTodo1.setProjectEntity(testProject);

        // Mocking: Repository는 모든 Todo를 반환한다고 가정
        List<TodoEntity> allTodos = Arrays.asList(completeTodo, incompleteTodo1, testTodo);
        when(todoRepository.findByProjectEntity_pno(anyInt())).thenReturn(allTodos);

        // TodoService의 getTodosByProjectEntity 시그니처가 loginUser를 받도록 수정되어야 합니다.
        // 현재는 편의상 pNo만 받도록 테스트합니다.

        // When
        List<TodoEntity> result = todoService.getTodosByProjectEntity(99);

        // Then
        // 결과 목록에 '완료' 상태의 Todo가 포함되지 않아야 합니다.
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(todo -> "완료".equals(todo.getState())));

        // ⭐️ 중요: 실제 구현에서는 프로젝트 멤버십 확인 로직(인가)도 여기서 테스트되어야 합니다.
    }
}