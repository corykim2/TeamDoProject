package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.dto.User.LoginRequest;
import com.TeamAA.TeamDo.dto.User.LoginResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.User.LoginService;
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

 @Operation(
         summary = "로그인",
         description = """
        /sessions/create<br>
        
        [결론]
        사용자 입력(id, password)을 검증하여 로그인 세션을 생성합니다.

        [사용 화면]
        - 로그인 페이지 > 로그인 버튼 클릭 시

        [로직 설명]
        1. 요청받은 userId에 해당하는 사용자가 DB에 존재하는지 조회합니다.
        2. 사용자가 존재할 경우, 입력된 password와 DB의 passwordHash를 비교하여 일치 여부를 검증합니다.
        3. 검증 통과 시 로그인 처리를 진행하고 세션에 사용자 정보를 저장합니다.
        4. 서버는 세션 ID를 포함한 JSESSIONID 쿠키를 클라이언트에 자동으로 발급합니다.

        [참조 테이블]
        - SELECT: user
        """
 )
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
