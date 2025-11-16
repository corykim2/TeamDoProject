package com.TeamAA.TeamDo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponse {
    @Schema(description = "회원 아이디", example = "user123")
    private String userId;
    @Schema(description = "응답 메시지", example = "회원가입 성공")
    private String message;

}
