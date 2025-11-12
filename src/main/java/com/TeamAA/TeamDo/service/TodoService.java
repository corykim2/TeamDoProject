package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Repository를 주입받기 위한 Lombok 어노테이션
public class TodoService {

    private final TodoRepository todoRepository; // Repository 주입

    // 1. 할 일 생성
    public TodoEntity createTodo(TodoEntity todo) {
        // ⭐️ 여기서 데이터 유효성 검사, 권한 확인 등 비즈니스 로직 수행
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
    //5. 할일 삭제
    public TodoEntity deleteTodo(Integer todoId){
        TodoEntity todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("해당 Todo가 존재하지 않습니다."));
        todoRepository.delete(todo);
        return todo;
    }
}