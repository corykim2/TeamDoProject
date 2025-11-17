package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Todo.TodoRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

//import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor // Repository를 주입받기 위한 Lombok 어노테이션
public class TodoService {

    private final TodoRepository todoRepository; // Repository 주입
    private final UserRepository userRepository;

    // 1. 할 일 생성
    public TodoEntity createTodo(TodoCreateRequest requestDto, UserEntity loginUser) {
        TodoEntity todo = new TodoEntity();
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

    // 4. 할 일 상태 업데이트
    public TodoEntity updateTodoState(Long todoId, String newState) {
        TodoEntity todo = getTodoById(todoId);
        todo.setState(newState);
        return todoRepository.save(todo);
    }
    //5. 할일 삭제(본인만 삭제가능 하도록 위해서는 로그인 기능 필요)
    public TodoEntity deleteTodo(Long todoId,UserEntity loginUser){
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("해당 Todo가 존재하지 않습니다."));
        String currentUserId = loginUser.getId();
        if (!todo.getCreatorId().getId().equals(currentUserId)) {
            //throw new AccessDeniedException("본인의 할 일만 삭제할 수 있습니다.");
        }
        todoRepository.delete(todo);
        return todo;
    }
    //6. 할일 수정
    public TodoEntity updateTodoFields(Long todoId, TodoUpdateRequest request,UserEntity loginUser) {
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("할 일을 찾을 수 없습니다."));

        if (!todo.getManagerId().equals(loginUser)) {
            //throw new AccessDeniedException("본인의 할 일만 수정할 수 있습니다.");
        }
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