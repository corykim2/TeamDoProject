package com.TeamAA.TeamDo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class TodoCreateRequest {

    @Schema(description = "할 일 이름", example = "api 개발")
    private String name;

    @Schema(description = "담당자 ID")
    private String managerId;

    @Schema(description = "마감일", example = "2025-11-20")
    private LocalDate deadline;

    @Schema(description = "우선순위 (1: 높음, 2: 약간 높음, 3: 중간, 4:약간 낮음, 5:낮음)", example = "1")
    @Min(1)
    @Max(5)
    private Integer priority;
}