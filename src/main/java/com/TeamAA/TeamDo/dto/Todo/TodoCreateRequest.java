package com.TeamAA.TeamDo.dto.Todo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*; // Import 추가
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoCreateRequest {

    @Schema(description = "프로젝트 id", example = "1")
    @NotNull(message = "프로젝트 ID는 필수입니다.") // 추가
    private Long pNo;

    @Schema(description = "할 일 이름", example = "api 개발")
    @NotBlank(message = "할 일 이름은 필수입니다.") // 추가
    private String name;

    @Schema(description = "담당자 ID", example = "user123")
    @NotBlank(message = "담당자 ID는 필수입니다.") // 추가
    private String managerId;

    @Schema(description = "마감일", example = "2025-11-20")
    @NotNull(message = "마감일은 필수입니다.") // 추가
    private LocalDate deadline;

    @Schema(description = "우선순위 (1: 높음, 2: 약간 높음, 3: 중간, 4:약간 낮음, 5:낮음)", example = "1")
    @NotNull(message = "우선순위는 필수입니다.") // 추가
    @Min(value = 1, message = "우선순위는 1 이상이어야 합니다.")
    @Max(value = 5, message = "우선순위는 5 이하이어야 합니다.")
    private Integer priority;
}