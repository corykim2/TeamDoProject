package com.TeamAA.TeamDo.repository.Project;

import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {
    List<ProjectEntity> findByTeamEntityName(String name);
    // Project의 teamEntity 필드 안에 있는 'name' 필드를 기준으로 찾기
}
