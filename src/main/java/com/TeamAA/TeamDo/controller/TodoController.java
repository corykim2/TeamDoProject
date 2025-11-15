package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 이 클래스가 REST API 컨트롤러임을 명시
@RequestMapping("/api/todos") // 기본 URL 경로 설정
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // POST /api/todos : 새로운 할 일 생성
    @PostMapping
    public ResponseEntity<TodoEntity> createTodo(@RequestBody TodoEntity todo) {
        TodoEntity createdTodo = todoService.createTodo(todo);
        return ResponseEntity.ok(createdTodo);
    }

    // GET /api/todos/{todoId} : 특정 할 일 조회
    @GetMapping("/{todoId}")
    public ResponseEntity<TodoEntity> getTodo(@PathVariable Integer todoId) {
        TodoEntity todo = todoService.getTodoById(todoId);
        return ResponseEntity.ok(todo);
    }

    // GET /api/todos/project/{pNo} : 프로젝트별 할 일 조회
    @GetMapping("/project/{pNo}")
    public ResponseEntity<List<TodoEntity>> getTodosByProject(@PathVariable Integer pNo) {
        List<TodoEntity> todos = todoService.getTodosByProjectEntity(pNo);
        return ResponseEntity.ok(todos);
    }

    // PUT /api/todos/{todoId}/state : 할 일 상태 업데이트
    @PutMapping("/{todoId}/state")
    public ResponseEntity<TodoEntity> updateTodoState(
            @PathVariable Integer todoId,
            @RequestBody String newState) {
        TodoEntity updatedTodo = todoService.updateTodoState(todoId, newState);
        return ResponseEntity.ok(updatedTodo);
    }
    //DELETE /api/todos/{todoId}:할일 삭제
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Integer todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.ok().build();
    }
    //PUT /api/todos/{todoId}:할일 수정
    @PatchMapping("/{todoId}")
    public ResponseEntity<TodoEntity> updateTodo(
            @PathVariable Integer todoId,
            @RequestBody TodoUpdateRequest request) {

        TodoEntity updatedTodo = todoService.updateTodoFields(todoId, request);
        return ResponseEntity.ok(updatedTodo);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TodoEntity>> getAllTodos(
            @RequestParam(defaultValue = "id") String sortBy, // 기본값: id
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Service 계층 호출 시 정렬 정보를 전달
        List<TodoEntity> todos = todoService.findAllTodosSorted(sortBy, direction);
        return ResponseEntity.ok(todos);
    }
}