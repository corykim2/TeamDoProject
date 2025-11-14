package com.TeamAA.TeamDo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectUpdateRequest {
    private String pname;  // 변경할 프로젝트 이름
    private Long teamId;   // 변경할 팀의 ID (팀을 옮길 경우)
}

