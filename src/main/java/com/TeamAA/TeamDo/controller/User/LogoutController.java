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
import com.TeamAA.TeamDo.service.SessionService;

@Tag(name = "유저관리", description = "유저관리 엔드포인트")
@RestController
@RequestMapping("/sessions")
public class LogoutController {

    @Autowired
    private SessionService sessionService;

    @Operation(summary = "로그아웃", description = "사용자 입력을 받아 로그아웃을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
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
    public ResponseEntity<String> logout(HttpSession session) {
        //정상 처리 로직
        try {
            String userId = sessionService.getUserId(session); //세션검증
            session.invalidate();
            return ResponseEntity.ok("로그아웃 완료");
        } catch (Exception e){
            // 내부 서버 예외
            throw new RuntimeException("로그아웃 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}