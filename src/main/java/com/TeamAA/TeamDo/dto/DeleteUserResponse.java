package com.TeamAA.TeamDo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteUserResponse {
    @Schema(description = "회원탈퇴 메세지", example = "회원탈퇴 완료")
    private String message;
}
