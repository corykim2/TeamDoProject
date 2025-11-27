package com.TeamAA.TeamDo.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    //입력데이터 공백처리
    @NotBlank(message = "아이디를 입력해주세요.")
    //사이즈 처리
    @Size(min = 6, max = 30, message = "아이디는 6자리 이상, 30자리 미만으로 입력해야 합니다.")
    @Schema(description = "회원 아이디", example = "user123")
    private String id;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 5, max = 16, message = "비밀번호는 5자리 이상, 16자리 미만으로 입력해야 합니다.")
    @Schema(description = "회원 비밀번호", example = "pass1234")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Size(max = 30, message = "이메일은 30자리 미만으로 입력해야 합니다.")
    @Schema(description = "회원 이메일", example = "user123@test.com")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 30, message = "올바른 이름을 입력해주세요.")
    @Schema(description = "회원 이름", example = "테스트유저")
    private String name;
}
