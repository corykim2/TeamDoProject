package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.ProjectRepository;
import com.TeamAA.TeamDo.repository.TodoRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
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
    public List<TodoEntity> getTodosByProjectEntity(Integer pNo) {
        return todoRepository.findByProjectEntity_pno(pNo); // Repository에서 선언한 메소드 사용
    }
    /*
    // 3. 프로젝트별 할 일 목록 조회 (필터링 및 인가 로직 가정 추가)
    public List<TodoEntity> getTodosByProjectEntity(Integer pNo, UserEntity loginUser) {
        // ⭐️ 1. 프로젝트 멤버십 확인 (ProjectService가 담당한다고 가정)
        // projectService.checkMembership(pNo, loginUser.getId());

        // ⭐️ 2. Todo 상태와 프로젝트 활성 상태를 기준으로 필터링
        // Repository 메서드가 findByProjectEntity_pnoAndStateNotAndProjectEntity_IsActive(pno, "완료", true) 라고 가정
        // return todoRepository.findByProjectEntity_pnoAndStateNotAndProjectEntity_IsActive(pNo, "완료", true);

        // 현재 Repository 메서드를 그대로 사용하며 필터링만 적용하는 임시 방안
        List<TodoEntity> todos = todoRepository.findByProjectEntity_pno(pNo);
        return todos.stream()
                .filter(todo -> !"완료".equals(todo.getState()))
                // .filter(todo -> todo.getProjectEntity().getIsActive()) // 프로젝트 활성 상태 필터링이 필요함
                .toList();
    }
    */
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