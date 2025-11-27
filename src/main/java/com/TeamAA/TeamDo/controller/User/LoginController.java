package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.dto.User.LoginRequest;
import com.TeamAA.TeamDo.dto.User.LoginResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저관리", description = "유저관리 엔드포인트")
@RestController
@RequestMapping("/sessions")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "로그인", description = "사용자 입력을 받아 DB에 저장된 user와 일치하는지 확인하고 로그인을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, 세션을 생성해서 브라우저에 쿠키로 전송",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = "{\"message\":\"로그인 성공\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "사용자 공백입력, 사용자 데이터 누락, user엔티티에 저장가능한 데이터 사이즈 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"올바른 아이디, 비밀번호를 입력해주세요.\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "DB에 저장된 아이디 또는 비밀번호와 사용자에게 받은 아이디 또는 비밀번호와 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"정확한 아이디와 비밀번호를 입력해주세요.\"}")
                    )),
            @ApiResponse(responseCode = "403", description = "탈퇴한 사용자, DB에서 withDrawn= true인 유저는 로그인이 불가능",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":403,\"message\":\"탈퇴한 사용자입니다.\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류, 예기지못한 오류 발생시 예외처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"로그인 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    // 로그인
    @PostMapping("")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        UserEntity user = loginService.login(request);
        session.setAttribute("userId", user.getId()); //세션생성 및 아이디 매칭
        LoginResponse response = new LoginResponse("로그인완료");
        return ResponseEntity.ok(response);
    }
}
