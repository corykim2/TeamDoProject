package com.TeamAA.TeamDo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TodoCreateRequest {
    private String name;        // 할 일 이름
    private Integer priority;   // 우선순위
    private LocalDate deadline;   // 마감일
    private String managerId;   // 담당자 ID
}
