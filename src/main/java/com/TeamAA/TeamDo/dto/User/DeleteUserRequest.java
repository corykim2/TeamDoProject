package com.TeamAA.TeamDo.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;
}
