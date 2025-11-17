package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Repository를 주입받기 위한 Lombok 어노테이션
public class TodoService {

    private final TodoRepository todoRepository; // Repository 주입

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
    public List<TodoEntity> getTodosByProjectEntity(Long pno) {
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
}