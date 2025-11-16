package com.TeamAA.TeamDo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Schema(description = "회원 아이디", example = "user123")
    private String id;
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;
}
