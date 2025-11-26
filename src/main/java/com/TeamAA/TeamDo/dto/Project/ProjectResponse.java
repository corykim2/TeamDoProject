package com.TeamAA.TeamDo.dto.Project;

import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectResponse {
    private Long pno;
    private String pname;
    private LocalDateTime createdTime;
    private String teamName; // TeamEntity의 이름
    private int progressPercent; // 0~100 사이의 진행률

    // 엔티티를 DTO로 변환하는 생성자
    public ProjectResponse(ProjectEntity entity, int progressPercent) {
        this.pno = entity.getPno();
        this.pname = entity.getPname();
        this.createdTime = entity.getCreatedTime();
        this.teamName = entity.getTeamEntity().getName(); // 팀 엔티티에서 이름만 추출
        this.progressPercent = progressPercent;
    }
}
