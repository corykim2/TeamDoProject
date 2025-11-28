package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.dto.User.SignupRequest;
import com.TeamAA.TeamDo.dto.User.SignupResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.User.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저관리", description = "유저관리 엔드포인트")
@RestController
@RequestMapping("/users")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @Operation(
            summary = "회원가입",
            description = """
        /users/create<br>
        
        [결론]
        입력된 사용자 정보를 검증한 뒤 신규 사용자 1건을 생성합니다.

        [사용 화면]
        - 회원가입 페이지 > [회원가입 완료] 버튼 클릭 시

        [로직 설명]
        1. 요청받은 userId, password, name, email 필수 입력값의 형식을 검증합니다.
        2. userId(또는 이메일)가 기존 DB에 이미 ,존재하는지 중복 여부를 조회합니다.
        3. 중복이 없으면 password를 서버에서 해시(BCrypt 등)하여 저장 가능한 형태로 변환합니다.
        4. 변환된 정보로 신규 사용자 엔티티를 생성하고 DB에 저장합니다.
        5. 회원가입 성공 결과를 클라이언트에 반환합니다.

        [참조 테이블]
        - INSERT: user
        - SELECT: user
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공, 비밀번호 해싱후 정상적으로 사용자의 정보를 DB에 저장,",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponse.class),
                            examples = @ExampleObject(value = "{\"userId\":\"user123\",\"message\":\"회원가입 성공\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "사용자 공백입력, 사용자 데이터 누락, user엔티티에 저장가능한 데이터 사이즈 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"올바른 정보를 입력해주세요\"}")
                    )),
            @ApiResponse(responseCode = "403", description = "탈퇴한 사용자, DB에서 withDrawn= true인 유저는 회원가입이 불가능",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":403,\"message\":\"탈퇴한 사용자입니다.\"}")
                    )),
            @ApiResponse(responseCode = "409", description = "DB에 저장된 id와 사용자의 입력값이 중복, DB에 저장된 email과 사용자의 입력값이 중복",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "아이디 중복", value = "{\"status\":409,\"message\":\"이미 존재하는 아이디입니다.\"}"),
                                    @ExampleObject(name = "이메일 중복", value = "{\"status\":409,\"message\":\"이미 존재하는 이메일입니다.\"}")
                            }
                    )),
//            @ApiResponse(responseCode = "409", description = "DB에 저장된 email과 사용자의 입력값이 중복",
//                  content = @Content(
//                            mediaType = "application/json",
//                           schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = "{\"status\":409,\"message\":\"이미 존재하는 이메일입니다.\"}")
//                   )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류, 예기지못한 오류 발생시 예외처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"회원가입 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    @PostMapping("")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserEntity user = signupService.signup(request);
        SignupResponse response = new SignupResponse(user.getId(), "회원가입 성공");
        return ResponseEntity.ok(response);
    }
}
