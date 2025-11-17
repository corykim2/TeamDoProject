package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.ErrorResponse;
import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.dto.SignupResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원가입", description = "회원가입 엔드포인트")
@RestController
@RequestMapping("/auth")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @Operation(summary = "회원가입", description = "사용자 정보를 받아 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponse.class),
                            examples = @ExampleObject(value = "{\"userId\":\"user123\",\"message\":\"회원가입 성공\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "입력값 오류, 입력값 중복",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"아이디 또는 비밀번호가 잘못 되었습니다.\"}")
                    )),
            @ApiResponse(responseCode = "403", description = "탈퇴한 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":403,\"message\":\"탈퇴한 사용자입니다.\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"회원가입 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        UserEntity user = signupService.signup(request);
        SignupResponse response = new SignupResponse(user.getId(), "회원가입 성공");
        return ResponseEntity.ok(response);
    }
}
