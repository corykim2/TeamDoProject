package com.TeamAA.TeamDo.dto.User;

import com.TeamAA.TeamDo.validation.BlankCheck;
import com.TeamAA.TeamDo.validation.SizeCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    //입력데이터 공백처리
    @NotBlank(message = "아이디를 입력해주세요.", groups = BlankCheck.class)
    //사이즈 처리
    @Size(min = 6, max = 30, message = "올바른 아이디를 입력해주세요.", groups = SizeCheck.class)
    @Schema(description = "회원 아이디", example = "user123")
    private String id;

    @NotBlank(message = "비밀번호를 입력해주세요.", groups = BlankCheck.class)
    @Size(min = 5, max = 16, message = "올바른 비밀번호를 입력해주세요.", groups = SizeCheck.class)
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;
}
