package com.TeamAA.TeamDo.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "로그인 메세지", example = "로그인 성공")
    private String message;
}