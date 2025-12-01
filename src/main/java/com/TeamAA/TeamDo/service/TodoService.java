package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Project.ProjectRepository;
import com.TeamAA.TeamDo.repository.Todo.TodoRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Repository를 주입받기 위한 Lombok 어노테이션
@Transactional
public class TodoService {

    private final TodoRepository todoRepository; // Repository 주입
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    private void checkCreatorAuthorization(TodoEntity todo, UserEntity loginUser) throws IllegalAccessException {
        if (!todo.getCreatorId().getId().equals(loginUser.getId())) {
            throw new IllegalAccessException("할 일 생성자만 이 작업을 수행할 수 있습니다."); // 더 구체적인 예외 사용
        }
    }

    private void checkManagerAuthorization(TodoEntity todo, UserEntity loginUser) throws IllegalAccessException {
        if (!todo.getManagerId().getId().equals(loginUser.getId())) {
            throw new IllegalAccessException("할 일 담당자만 이 작업을 수행할 수 있습니다.");
        }
    }

    // 1. 할 일 생성
    public TodoEntity createTodo(TodoCreateRequest requestDto, UserEntity loginUser) {
        TodoEntity todo = new TodoEntity();

        todo.setProjectEntity(projectRepository.findByPno(requestDto.getPNo()));
        todo.setName(requestDto.getName());
        todo.setDeadline(requestDto.getDeadline());
        todo.setPriority(requestDto.getPriority());
        todo.setCreatorId(loginUser);

        UserEntity manager = userRepository.findById(requestDto.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException("담당자를 찾을 수 없습니다."));
        todo.setManagerId(manager);
        todo.setState("미완");

        return todoRepository.save(todo);
    }


    // 2. 특정 할 일 조회
    public TodoEntity getTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Todo가 존재하지 않습니다." ));
    }

    // 3. 프로젝트별 할 일 목록 조회
    public List<TodoEntity> getTodosByProjectEntity(Long pNo, UserEntity loginUser) {

        // 1. ⭐️ 인가 로직: 사용자가 해당 프로젝트의 멤버인지 확인
        projectService.validateUserInProject(pNo, loginUser.getId());

        // 2. ⭐️ 필터링 로직: Repository에서 직접 조건에 맞는 데이터만 조회
        // 가정: Repository에 findByProjectEntity_pnoAndStateNotAndProjectEntity_IsActive 같은 메서드가 정의됨
        // (완료 상태를 제외하고, 프로젝트가 활성화된 상태인 Todo만 조회)

        try {
            // 현재는 '완료' 상태만 제외하고 조회하는 것으로 가정 (주석 코멘트 반영)
            return todoRepository.findByProjectEntity_pnoAndStateNot(pNo, "DONE");

        } catch (Exception e) {
            // 프로젝트가 존재하지 않거나(404) 등의 예외를 처리할 수 있음.
            throw new RuntimeException("프로젝트 할 일 조회 중 오류 발생", e);
        }
    }

    // 4. 할 일 상태 업데이트
    public TodoEntity updateTodoState(Long todoId, String newState,UserEntity loginUser) throws IllegalAccessException {
        TodoEntity todo = getTodoById(todoId);
        checkManagerAuthorization(todo, loginUser);
        todo.setState(newState);
        return todoRepository.save(todo);
    }
    //5. 할일 삭제
    public void deleteTodo(Long todoId, UserEntity loginUser) throws IllegalAccessException {
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("해당 Todo가 존재하지 않습니다."));
        checkCreatorAuthorization(todo, loginUser);
        todoRepository.delete(todo);
    }
    //6. 할일 수정
    public TodoEntity updateTodoFields(Long todoId, TodoUpdateRequest request,UserEntity loginUser) throws IllegalAccessException {
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("할 일을 찾을 수 없습니다."));
        checkCreatorAuthorization(todo, loginUser);
        if (request.getName() != null) {
            todo.setName(request.getName());
        }
        if (request.getDeadline() != null) {
            todo.setDeadline(request.getDeadline());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        return todoRepository.save(todo);
    }
    //정렬기능
    public List<TodoEntity> findAllTodosSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);

        return todoRepository.findAll(sort);
    }
}