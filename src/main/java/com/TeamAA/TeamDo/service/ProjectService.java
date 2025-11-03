package com.TeamAA.TeamDo.service;

import java.util.List;
import com.TeamAA.TeamDo.dto.ProjectCreateRequestDto;
import com.TeamAA.TeamDo.entity.Project;
import com.TeamAA.TeamDo.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    public Project createProject(ProjectCreateRequestDto dto) {

        Project newProject = Project.builder()
                .pname(dto.getPname())
                .teamCode(dto.getTeamCode())
                .build();

        return projectRepository.save(newProject);
    }

    /**
     * [새로 추가] pno(ID)로 프로젝트 조회
     * (컨트롤러에서 이 메서드를 호출하고 있습니다)
     */
    public Project getProjectByPno(Integer pno) {
        return projectRepository.findById(pno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project PNO: " + pno));
    }

    // -- (아래는 이미 잘 만드신 코드) --

    /**
     * 프로젝트 삭제
     */
    public void deleteProject(Integer pno) {
        projectRepository.deleteById(pno);
    }

    /**
     * 팀 코드로 프로젝트 리스트 조회
     * (이 기능을 사용하려면 ProjectRepository에 findByTeamCode 메서드 정의 필요)
     */
    public List<Project> getProjectsByTeamCode(String teamCode) {
        // [주의] 이 기능을 쓰려면 ProjectRepository에 쿼리 메서드 추가가 필요합니다.
        // return projectRepository.findByTeamCode(teamCode);

        // 임시로 빈 리스트 반환 (findByTeamCode 구현 전까지)
        return List.of();
    }
}

