package com.TeamAA.TeamDo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @Schema(description = "회원 아이디", example = "user123")
    private String id;
    @Schema(description = "회원 이메일", example = "user123@example.com")
    private String email;
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;
    @Schema(description = "회원 이름", example = "김이박")
    private String name;
}
