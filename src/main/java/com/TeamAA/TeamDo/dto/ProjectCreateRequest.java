package com.TeamAA.TeamDo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCreateRequest {
    private String pname; // 프로젝트명

    // "teamId" (PK)를 받음
    private Long teamId;

    // "userEntity"를 연결하기 위한 "userId" (PK)를 받음
    private String  userId;
}
