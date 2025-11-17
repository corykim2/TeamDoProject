package com.TeamAA.TeamDo.controller.Todo;

//import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.Todo.TodoStateUpdateRequest;
import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Todo.TodoRepository;
import com.TeamAA.TeamDo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Todo", description = "할 일(Todo) 관련 API")
@RestController // 이 클래스가 REST API 컨트롤러임을 명시
@RequestMapping("/api/todos") // 기본 URL 경로 설정
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    @Autowired
    private TodoRepository todoRepository;

    // POST /api/todos : 새로운 할 일 생성
    @Operation(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<?> createTodo(@RequestBody TodoCreateRequest requestDto, HttpServletRequest request) {
        UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");

        TodoEntity savedTodo = todoService.createTodo(requestDto, loginUser);
        return ResponseEntity.ok(savedTodo);
    }

    // GET /api/todos/{todoId} : 특정 할 일 조회
    @Operation(summary = "특정 할 일 조회", description = "특정 할 일을 조회합니다.")
    @GetMapping("/{todoId}")
    public ResponseEntity<TodoEntity> getTodo(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,HttpServletRequest request) {
        //UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");
        TodoEntity todo = todoService.getTodoById(todoId);
        return ResponseEntity.ok(todo);
    }

    // GET /api/todos/project/{pNo} : 프로젝트별 할 일 조회
    @Operation(summary = "프로젝트별 할 일 조회", description = "프로젝트별 할 일을 조회합니다.")
    @GetMapping("/project/{pNo}")
    public ResponseEntity<List<TodoEntity>> getTodosByProject(
            @Parameter (description="프로젝트 번호")
            @PathVariable Integer pno,HttpServletRequest request) {
        UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");
        //ProjectService.isUserInProject(pno, loginUser.getId());
        //프로젝트에 사용자가 참여하는지 확인하는 기능 필요
        List<TodoEntity> todos = todoService.getTodosByProjectEntity(pno);
        List<TodoEntity> userTodos = todos.stream()
                .filter(todo -> todo.getCreatorId().getId().equals(loginUser.getId()))
                .toList();

        return ResponseEntity.ok(userTodos);
    }

    // PUT /api/todos/{todoId}/state : 할 일 상태 업데이트
    @Operation(summary = "할 일 상태 업데이트", description = "할 일 상태를 업데이트합니다.")
    @PutMapping("/{todoId}/state")
    public ResponseEntity<?> updateTodoState(
            @Parameter(description = "할 일 ID,할 일 상태 업데이트 요청 DTO")
            @PathVariable Long todoId,
            @RequestBody TodoStateUpdateRequest requestDto,
            HttpServletRequest request) {
        UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");
        TodoEntity todo = todoService.getTodoById(todoId);
        if (!todo.getCreatorId().getId().equals(loginUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 할 일만 상태수정 가능합니다.");
        }
        String newState = requestDto.getState();
        TodoEntity updatedTodo = todoService.updateTodoState(todoId, newState);
        return ResponseEntity.ok(updatedTodo);
    }
    //DELETE /api/todos/{todoId}:할일 삭제
    @Operation(summary = "할 일 삭제", description = "할 일을 삭제합니다.")
    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,HttpServletRequest request) {
        UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");
        todoService.deleteTodo(todoId,loginUser);
        return ResponseEntity.ok().build();
    }

    //PUT /api/todos/{todoId}:할일 수정
    @Operation(summary = "할 일 수정", description = "할 일을 수정합니다.")
    @PatchMapping("/{todoId}")
    public ResponseEntity<?> updateTodo(
            @Parameter(description = "할 일 ID,할 일 업데이트 요청 DTO")
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequest UpdateRequest,HttpServletRequest request) {
        UserEntity loginUser = (UserEntity) request.getAttribute("loginUser");
        TodoEntity todo = todoService.getTodoById(todoId);
        if (!todo.getCreatorId().getId().equals(loginUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 할 일만 수정 가능합니다.");
        }
        TodoEntity updatedTodo = todoService.updateTodoFields(todoId, UpdateRequest,loginUser);
        return ResponseEntity.ok(updatedTodo);
    }


    //GET api/todos:할일 정렬
    @Operation(summary = "할 일 정렬", description = "할 일을 정렬합니다.")
    @GetMapping("/todos")
    public ResponseEntity<List<TodoEntity>> getAllTodos(
            @Parameter(description = "정렬 기준 속성 및 방향")
            @RequestParam(defaultValue = "id") String sortBy, // 기본값: id
            @RequestParam(defaultValue = "asc") String direction,HttpServletRequest request)
    {// Service 계층 호출 시 정렬 정보를 전달, 프로젝트 멤버확인필요
        List<TodoEntity> todos = todoService.findAllTodosSorted(sortBy, direction);
        return ResponseEntity.ok(todos);
    }
}