package com.TeamAA.TeamDo.controller.User;

import com.TeamAA.TeamDo.dto.User.DeleteUserRequest;
import com.TeamAA.TeamDo.dto.User.DeleteUserResponse;
import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.DeleteUserService;
import com.TeamAA.TeamDo.service.SessionService;
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

    @Operation(summary = "회원탈퇴", description = "사용자의 세션을 검증한 후 비밀번호 받아 DB에 저장된 비밀번호화 일치 한지 확인후 회원탈퇴를 진행합니다.")
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
            @ApiResponse(responseCode = "401", description = "사용자가 입력한 비밀번호와 DB에 저장된 사용자의 비밀번호와 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"비밀번호가 일치하지않습니다.\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류, 예기지못한 오류 발생시 예외처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"회원탈퇴 처리 중 문제가 발생했습니다.\"}")
                    ))
    })
    @DeleteMapping("/me")
    public ResponseEntity<DeleteUserResponse> deleteUser(@Valid @RequestBody DeleteUserRequest request, HttpSession session) {
        String userId = sessionService.getUserId(session); //세션검증
        UserEntity user = deleteUserService.deleteUser(userId, request);
        session.invalidate();
        DeleteUserResponse response = new DeleteUserResponse("회원 탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}