package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.entity.ProjectEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.ProjectRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Repository를 주입받기 위한 Lombok 어노테이션
public class TodoService {

    private final TodoRepository todoRepository; // Repository 주입
    private final ProjectRepository projectRepository;// 프로젝트  Repository 주입
    private final UserRepository userRepository;// 유저 Repository 주입

    // 1. 할 일 생성
    public TodoEntity createTodo(TodoEntity todo) {
        return todoRepository.save(todo);
    }

    // 2. 특정 할 일 조회
    public TodoEntity getTodoById(Integer todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Todo가 존재하지 않습니다." ));
    }

    // 3. 프로젝트별 할 일 목록 조회
    public List<TodoEntity> getTodosByProjectEntity(Integer pno) {
        return todoRepository.findByProjectEntity_pno(pno); // Repository에서 선언한 메소드 사용
    }

    // 4. 할 일 상태 업데이트
    public TodoEntity updateTodoState(Integer todoId, String newState) {
        TodoEntity todo = getTodoById(todoId);
        // ⭐️ 상태 변경 로직
        todo.setState(newState);
        return todoRepository.save(todo);
    }
    //5. 할일 삭제(본인만 삭제가능 하도록 위해서는 로그인 기능 필요)
    public TodoEntity deleteTodo(Integer todoId){
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("해당 Todo가 존재하지 않습니다."));
        todoRepository.delete(todo);
        return todo;
    }
    //6. 할일 수정
    public TodoEntity updateTodoFields(Integer todoId, TodoUpdateRequest request) {
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("할 일을 찾을 수 없습니다."));
/*
//로그인기능 필요
        if (!todo.getMangerId().equals(currentUserId)) {
            // 권한이 없는 경우, HTTP 403 Forbidden 에러를 발생시킬 예외를 던집니다.
            throw new AccessDeniedException("수정 권한이 없습니다. 본인의 할 일만 수정할 수 있습니다.");
        }
*/
        if (request.getName() != null) {
            todo.setName(request.getName());
        }
        if (request.getDeadline() != null) {
            todo.setDeadline(request.getDeadline());
        }
        if (request.getState() != null) {
            todo.setState(request.getState());
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

    @Transactional //프로젝트 ID와 DTO를 받아 To-Do를 생성하고 "연결"하는 메서드
    public TodoEntity createTodoForProject(Integer projectId, TodoCreateRequest dto) {

        // 1. 부모 프로젝트 조회
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + projectId));

        // 2. 담당 매니저(User) 조회 (User의 PK는 String)
        UserEntity manager = userRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + dto.getManagerId()));

        // 3. new + Setters 사용 (TodoEntity의 틀을 유지)
        TodoEntity newTodo = new TodoEntity();
        newTodo.setProjectEntity(project); // (중요) 부모 프로젝트 "연결"
        newTodo.setUserEntity(manager);    // (중요) 담당자 "연결"
        newTodo.setName(dto.getName());
        newTodo.setPriority(dto.getPriority());
        newTodo.setDeadline(dto.getDeadline());
        newTodo.setState("TODO"); // (중요) To-Do의 초기 상태를 "TODO"로 설정

        // 4. To-Do 저장
        return todoRepository.save(newTodo);
    }
}