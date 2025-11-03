package com.TeamAA.TeamDo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCreateRequestDto {
    // 1. 생성 시 사용자에게 받을 데이터
    private String pname;
    private String teamCode;
}
