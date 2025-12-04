package com.TeamAA.TeamDo.dto.Todo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank; // Import 추가
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoStateUpdateRequest {

    @Schema(description = "할 일 상태", example = "완료")
    @NotBlank(message = "변경할 상태 값은 필수입니다.") // 추가
    private String state;
}