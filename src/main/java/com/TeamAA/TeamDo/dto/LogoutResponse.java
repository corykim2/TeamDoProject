package com.TeamAA.TeamDo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogoutResponse {
    @Schema(description = "로그아웃 메세지", example = "로그아웃 완료")
    private String message;
}
