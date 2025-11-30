package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.dto.User.LogoutResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamAA.TeamDo.service.User.SessionService;

@Tag(name = "유저관리", description = "유저관리 엔드포인트")
@RestController
@RequestMapping("/sessions")
public class LogoutController {

    @Autowired
    private SessionService sessionService;

    @Operation(
            summary = "로그아웃",
            description = """
                    /sessions/delete<br>
                    
                    [결론]
                    현재 로그인 상태의 세션을 종료하여 사용자 인증 상태를 해제합니다.
                    
                    [사용 화면]
                    - 마이페이지 > [로그아웃] 버튼 클릭 시
                    
                    [로직 설명]
                    1. 클라이언트 요청에 포함된 JSESSIONID 쿠키를 통해 현재 활성화된 세션을 식별합니다.
                    2. 세션이 존재하면 HttpSession.invalidate()를 호출하여 세션을 종료합니다.
                       - 세션에 저장된 사용자 정보(userId 등)가 모두 삭제됩니다.
                    3. 세션 종료 후, 클라이언트는 더 이상 인증된 사용자로 인식되지 않습니다.
                    
                    [참조 테이블]
                    - DB사용 X (세션 기반 처리)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공, 세션 객체 제거, 더이상 접근불가능",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LogoutResponse.class),
                            examples = @ExampleObject(value = "{\"message\":\"로그아웃 완료\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "세션이 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"세션이 만료되었습니다.\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"로그아웃 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    @DeleteMapping("")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(new ErrorResponse(401, "세션이 만료되었습니다."));
            }
            session.invalidate();
            return ResponseEntity.ok(new LogoutResponse("로그아웃 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponse(500, "로그아웃 처리 중 문제가 발생하였습니다."));
        }
    }
}