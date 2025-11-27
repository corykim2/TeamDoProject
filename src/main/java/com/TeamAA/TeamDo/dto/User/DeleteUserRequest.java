package com.TeamAA.TeamDo.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequest {
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;
}
