package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByTeamCode(String teamCode); // 팀 코드로 프로젝트 조회
}
