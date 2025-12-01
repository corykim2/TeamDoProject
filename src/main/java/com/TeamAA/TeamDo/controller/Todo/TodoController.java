package com.TeamAA.TeamDo.controller.Todo;

import com.TeamAA.TeamDo.dto.Todo.TodoReadResponse;
import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.dto.Todo.TodoStateUpdateRequest;
import com.TeamAA.TeamDo.dto.TodoUpdateRequest;
import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.TodoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "Todo", description = "할 일(Todo) 등록,조회,수정,삭제 API")
@RestController // 이 클래스가 REST API 컨트롤러임을 명시
@RequestMapping("/api") // 기본 URL 경로 설정
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // POST /api/registration : 새로운 할 일 생성
    @Operation(
            summary = "할 일 생성",
            description = """
            [결론]
            할 일 이름, 담당자, 마감일, 우선순위를 입력받아 새로운 할 일을 생성합니다.

            [사용 화면]
            - 프로젝트 상세 페이지 내 '할 일 추가' 모달 또는 버튼

            [로직 설명]
            1. 요청 DTO를 검증하고 로그인된 사용자 정보를 할 일 생성자의 ID로 설정합니다.
            2. 새로운 To-Do 엔티티를 데이터베이스에 저장합니다.

            [참조 테이블]
            - INSERT: todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "할 일 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (필수 필드 누락 등)"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            }
    )
    @PostMapping("/todos/registration")
    public ResponseEntity<TodoReadResponse> createTodo(@RequestBody TodoCreateRequest requestDto,
                                                       @RequestAttribute("loginUser") UserEntity loginUser) {
        TodoEntity savedTodo = todoService.createTodo(requestDto, loginUser);
        TodoReadResponse responseDto = TodoReadResponse.fromEntity(savedTodo);
        return ResponseEntity.ok(responseDto);
    }

    // GET /api/todos/details/by-todoId/{todoId} : 특정 할 일 조회
    @Operation(
            summary = "특정 할 일 상세 조회 (detail by todoId)",
            description = """
            [결론]
            특정 할 일 ID(todoId)를 이용해 해당 할 일의 상세 정보를 반환합니다.

            [사용 화면]
            - 할 일 목록 > 특정 할 일 클릭 시 상세/수정/삭제 모달 또는 페이지

            [로직 설명]
            1. todoId로 To-Do 엔티티를 조회합니다.
            2. 상세 정보(생성자, 생성시간 등)를 포함한 응답을 반환합니다.

            [참조 테이블]
            - SELECT: todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                    @ApiResponse(responseCode = "404", description = "요청된 할 일(todoId)을 찾을 수 없음")
            }
    )
    @GetMapping("/todos/details/by-todoId/{todoId}")
    public ResponseEntity<TodoReadResponse> getTodo(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,@RequestAttribute("loginUser") UserEntity loginUser) {
        TodoEntity todo = todoService.getTodoById(todoId);
        TodoReadResponse responseDto = TodoReadResponse.fromEntity(todo);
        return ResponseEntity.ok(responseDto);
    }

    // GET /api/todos-list/by-project/{pNo} : 프로젝트별 할 일 조회
    @Operation(
            summary = "프로젝트별 할 일 목록 조회 (list by pNo)",
            description = """
            [결론]
            프로젝트 고유 번호(pNo)에 속한 모든 팀원의 할 일 목록 중 로그인 사용자가 생성한 할 일만 필터링하여 반환합니다.

            [사용 화면]
            - 프로젝트 상세 페이지의 할 일 목록 섹션

            [로직 설명]
            1. pNo를 사용하여 해당 프로젝트에 속한 모든 To-Do 목록을 가져옵니다.
            2. **(중요)** 가져온 목록 중 현재 로그인 사용자가 생성한 할 일만 필터링합니다. (현재 로직 기준)
            3. 프로젝트에 사용자가 참여하는지 확인하는 추가 로직이 필요합니다.

            [참조 테이블]
            - SELECT: project, todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                    @ApiResponse(responseCode = "404", description = "요청된 프로젝트(pNo)를 찾을 수 없음")
            }
    )
    @GetMapping("/todos-list/by-project/{pNo}")
    public ResponseEntity<List<TodoReadResponse>> getTodosByProject(
            @Parameter (description="프로젝트 번호")
            @PathVariable Long pno,@RequestAttribute("loginUser") UserEntity loginUser) {
        // 1. 서비스 호출: 프로젝트에 속한 모든 할 일 조회
        List<TodoEntity> todos = todoService.getTodosByProjectEntity(pno, loginUser);

        // 2. 전체 목록을 DTO로 변환
        List<TodoReadResponse> allProjectTodos = todos.stream()
                .map(TodoReadResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(allProjectTodos);
    }

    // PUT /api/todos/modification-state/by-todoId/{todoId}/state: 할 일 상태 업데이트
    @Operation(
            summary = "할 일 상태 변경(state modification by todoId)",
            description = """
            [결론]
            특정 할 일의 상태(예: TODO, IN_PROGRESS, DONE)를 업데이트합니다.

            [사용 화면]
            - 메인페이지 또는 프로젝트 상세 페이지의 To-Do 목록 카드 드래그앤드롭/버튼

            [로직 설명]
            1. todoId와 요청된 새로운 상태(newState)를 전달받습니다.
            2. **담당자만** 상태 변경이 가능한지 권한을 확인합니다.
            3. To-Do 엔티티의 상태 필드를 업데이트하고 저장합니다.

            [참조 테이블]
            - UPDATE: todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "상태 업데이트 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 상태 값 요청 또는 요청 데이터 오류"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                    @ApiResponse(responseCode = "403", description = "권한 없음 (로그인 사용자가 담당자가 아님)"),
                    @ApiResponse(responseCode = "404", description = "요청된 할 일(todoId)을 찾을 수 없음")
            }
    )
    @PutMapping("/todos/modification-state/by-todoId/{todoId}/state")
    public ResponseEntity<TodoReadResponse> updateTodoState(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,
            @RequestBody TodoStateUpdateRequest requestDto,
            @RequestAttribute("loginUser") UserEntity loginUser) throws IllegalAccessException {
        String newState = requestDto.getState();
        TodoEntity updatedTodo = todoService.updateTodoState(todoId, newState,loginUser);
        TodoReadResponse responseDto = TodoReadResponse.fromEntity(updatedTodo);
        return ResponseEntity.ok(responseDto);
    }

    //DELETE /api/todos/removal/by-todoId/{todoId}:할일 삭제
    @Operation(
            summary = "할 일 삭제 (removal by todoId)",
            description = """
            [결론]
            특정 할 일 ID를 이용해 해당 할 일을 삭제합니다.

            [사용 화면]
            - 할 일 상세 모달/페이지의 '삭제' 버튼

            [로직 설명]
            1. todoId를 이용해 To-Do 엔티티를 조회합니다.
            2. **생성자만** 삭제가 가능한지 권한을 확인합니다.
            3. 해당 To-Do 엔티티를 데이터베이스에서 삭제합니다.

            [참조 테이블]
            - DELETE: todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "할 일 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                    @ApiResponse(responseCode = "403", description = "권한 없음 (로그인 사용자가 생성자가 아님)"),
                    @ApiResponse(responseCode = "404", description = "요청된 할 일(todoId)을 찾을 수 없음")
            }
    )
    @DeleteMapping("/todos/removal/by-todoId/{todoId}")
    public ResponseEntity<?> deleteTodo(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,@RequestAttribute("loginUser") UserEntity loginUser) throws IllegalAccessException {
        todoService.deleteTodo(todoId,loginUser);
        return ResponseEntity.ok().build();
    }

    //PUT /api/modification/by-todoId/{todoId}:할일 수정
    @Operation(
            summary = "할 일 내용 수정 (modification by todoId)",
            description = """
            [결론]
            할 일 이름, 담당자, 마감일, 우선순위 등 주요 필드를 수정합니다.

            [사용 화면]
            - 할 일 상세 모달/페이지의 '수정' 버튼

            [로직 설명]
            1. todoId를 이용해 To-Do 엔티티를 조회하고 요청된 업데이트 내용을 전달받습니다.
            2. **생성자만** 수정이 가능한지 권한을 확인합니다.
            3. 요청된 필드(이름, 담당자, 마감일, 우선순위)를 업데이트하고 저장합니다.

            [참조 테이블]
            - UPDATE: todo
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (필수 필드 누락 등)"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                    @ApiResponse(responseCode = "403", description = "권한 없음 (로그인 사용자가 생성자가 아님)"),
                    @ApiResponse(responseCode = "404", description = "요청된 할 일(todoId)을 찾을 수 없음")
            }
    )
    @PatchMapping("/todos/modification/by-todoId/{todoId}")
    public ResponseEntity<TodoReadResponse> updateTodo(
            @Parameter(description = "할 일 ID")
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequest UpdateRequest,@RequestAttribute("loginUser") UserEntity loginUser) throws IllegalAccessException {
        TodoEntity updatedTodo = todoService.updateTodoFields(todoId, UpdateRequest,loginUser);
        TodoReadResponse responseDto = TodoReadResponse.fromEntity(updatedTodo);
        return ResponseEntity.ok(responseDto);
    }


    //GET api/todos:할일 정렬
    @Operation(summary = "할 일 정렬 (내부 사용)", description = "할 일 목록을 정렬 기준 속성 및 방향에 따라 조회합니다.")
    @Hidden // 이 API는 내부 로직 또는 특정 용도로 사용되므로 Swagger UI에서 숨깁니다.
    @GetMapping("/todos")
    public ResponseEntity<List<TodoReadResponse>> getAllTodos( // ⭐️ DTO 리스트 반환으로 수정
                                                               @Parameter(description = "정렬 기준 속성 (예: id, dueDate)")
                                                               @RequestParam(defaultValue = "id") String sortBy,
                                                               @Parameter(description = "정렬 방향 (asc 또는 desc)")
                                                               @RequestParam(defaultValue = "asc") String direction,
                                                               @RequestAttribute("loginUser") UserEntity loginUser) { // ⭐️ @RequestAttribute 사용

        // ❌ 내부 로직 제거

        List<TodoEntity> todos = todoService.findAllTodosSorted(sortBy, direction);

        // ⭐️ DTO 리스트로 변환
        List<TodoReadResponse> responseList = todos.stream()
                .map(TodoReadResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(responseList);
    }
}