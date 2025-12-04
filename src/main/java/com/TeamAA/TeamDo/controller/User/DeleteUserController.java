package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.DeleteUserRequest;
import com.TeamAA.TeamDo.dto.User.DeleteUserResponse;
import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.User.DeleteUserService;
import com.TeamAA.TeamDo.service.User.SessionService;
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
@RequestMapping("/users")
public class DeleteUserController {

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private SessionService sessionService;
    @Operation(
            summary = "회원탈퇴",
            description = """
        /users/delete<br>
        
        [결론]
        로그인된 사용자가 본인 인증(비밀번호 확인)에 성공하면 계정 상태를 ‘탈퇴’로 업데이트합니다.

        [사용 화면]
        - 마이페이지 > [회원탈퇴] 버튼 클릭 시

        [로직 설명]
        1. 요청에 포함된 JSESSIONID 쿠키를 이용해 현재 세션을 조회합니다.
        2. 세션에 저장된 사용자 정보(userId)를 기반으로 탈퇴 대상 계정을 식별합니다.
        3. 클라이언트가 입력한 비밀번호를 DB에 저장된 passwordHash와 비교하여 본인 여부를 검증합니다.
        4. 비밀번호 검증에 성공하면 해당 사용자 레코드의 withDrawn 값을 true 로 변경하여 논리적 삭제를 처리합니다.
        5. 탈퇴 처리 이후 HttpSession.invalidate()를 호출하여 세션을 종료합니다.

        [참조 테이블]
        - UPDATE: user (withDrawn = true)
        - SELECT: user
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원탈퇴 성공, DB에 저장된 withDrawn = true 로 변경하는 논리적 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeleteUserResponse.class),
                            examples = @ExampleObject(value = "{\"message\":\"회원탈퇴 완료\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "비밀번호 공백입력, 비밀번호 누락",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"비밀번호를 입력해주세요\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패(세션 없음/세션 만료/비밀번호 불일치)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "SESSION_INVALID",
                                            value = "{\"status\":401,\"message\":\"로그인 세션이 유효하지 않습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "PASSWORD_MISMATCH",
                                            value = "{\"status\":401,\"message\":\"비밀번호가 일치하지 않습니다.\"}"
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류, 예기지못한 오류 발생시 예외처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"회원탈퇴 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    @DeleteMapping("")
    public ResponseEntity<DeleteUserResponse> deleteUser(@Valid @RequestBody DeleteUserRequest request, HttpSession session) {
        String userId = sessionService.getUserId(session); //세션검증
        UserEntity user = deleteUserService.deleteUser(userId, request);
        session.invalidate();
        DeleteUserResponse response = new DeleteUserResponse("회원 탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}