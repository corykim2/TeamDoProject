package com.TeamAA.TeamDo.dto.Todo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoStateUpdateRequest {

    @Schema(description = "할 일 상태", example = "완료")
    private String state;
}
