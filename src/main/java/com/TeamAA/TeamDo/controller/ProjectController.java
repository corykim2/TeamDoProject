package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.ProjectCreateRequest;
import com.TeamAA.TeamDo.dto.ProjectResponse;
import com.TeamAA.TeamDo.dto.ProjectUpdateRequest;
import com.TeamAA.TeamDo.entity.ProjectEntity;
import com.TeamAA.TeamDo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트(Project) API", description = "프로젝트의 등록, 조회, 수정, 삭제 및 진행률 계산 기능을 제공합니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트 등록 (Create)
     */
    @Operation(
            summary = "프로젝트 생성",
            description = """
            [결론]
            새로운 프로젝트 1건을 생성하고 저장합니다.
            
            [사용 화면]
            - 팀 대시보드 > [새 프로젝트 만들기] 버튼 클릭 시
            
            [로직 설명]
            1. 요청받은 managerId(담당자)와 teamId(팀)가 DB에 존재하는지 검증합니다.
            2. 검증 통과 시, 입력된 정보로 프로젝트를 생성합니다.
            
            [참조 테이블]
            - INSERT: project
            - SELECT: user, team
            """
    )
    @PostMapping("/project/registration")
    public ProjectEntity createProject(@RequestBody ProjectCreateRequest requestDto) {
        return projectService.createProject(requestDto);
    }

    /**
     * 프로젝트 상세 조회 (Read - Detail)
     */
    @Operation(
            summary = "프로젝트 상세 정보 조회 (Detail by Pno)",
            description = """
            [결론]
            프로젝트 고유 번호(pno)를 이용해 프로젝트의 상세 정보와 현재 진행률을 반환합니다.
            
            [사용 화면]
            - 프로젝트 리스트 > 특정 프로젝트 카드 클릭 시 상세 페이지
            
            [로직 설명]
            1. pno로 프로젝트 엔티티를 조회합니다.
            2. 해당 프로젝트 하위의 To-Do 목록을 분석하여 '완료됨(DONE)' 상태의 비율을 계산합니다.
            3. 계산된 진행률(progressPercent)을 포함한 응답 DTO를 반환합니다.
            
            [참조 테이블]
            - SELECT: project, todo
            """
    )
    @GetMapping("/project/detail/by-pno/{pno}")
    public ProjectResponse getProject(@PathVariable Long pno) {
        return projectService.getProjectByPno(pno);
    }

    /**
     * 프로젝트 정보 수정 (Update)
     */
    @Operation(
            summary = "프로젝트 정보 수정 (Modification by Pno)",
            description = """
            [결론]
            기존 프로젝트의 이름이나 소속 팀을수정합니다.
            
            [사용 화면]
            - 프로젝트 상세 > 설정 > [정보 수정] 팝업
           
            [로직 설명]
            1. 권한 검증: 요청자(userId)가 해당 프로젝트 팀의 멤버인지 확인합니다. (미참여 시 예외 발생)
            2. 프로젝트 이름(pname) 또는 소속 팀(teamId)을 변경합니다.
            
            
            [참조 테이블]
            - UPDATE: project
            - SELECT: team_participating, project
            """
    )
    @PutMapping("/project/modification/by-pno/{pno}")
    public ProjectEntity updateProject(@PathVariable Long pno,
                                       @RequestBody ProjectUpdateRequest requestDto,
                                       @RequestParam String userId) {

        projectService.validateUserInProject(pno, userId);
        return projectService.updateProject(pno, requestDto);
    }

    /**
     * 프로젝트 삭제 (Delete)
     */
    @Operation(
            summary = "프로젝트 영구 삭제 (Removal by Pno)",
            description = """
            [결론]
            프로젝트 1건을 DB에서 영구적으로 삭제합니다.
            
            [사용 화면]
            - 프로젝트 상세 > 설정 > [프로젝트 삭제] 
            
            [주의사항]
            - Cascade Delete: 프로젝트가 삭제되면, 하위에 연결된 모든 To-Do 데이터도 함께 삭제됩니다.
            - 권한 검증: 요청자(userId)가 팀 멤버여야 합니다.
            
            [참조 테이블]
            - DELETE: project, todo
            """
    )
    @DeleteMapping("/project/removal/by-pno/{pno}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long pno,
                                              @RequestParam(required = false) String userId) {
        // (참고: 삭제 시에도 권한 검증을 하려면 userId가 필요합니다. 일단 required=false로 두었습니다.)

        if (userId != null) {
            projectService.validateUserInProject(pno, userId);
        }
        projectService.deleteProject(pno);
        return ResponseEntity.ok().build();
    }

    // =================================================================================
    // 2. 목록 관리 (Project List - Plural)
    // =================================================================================

    /**
     * 팀별 프로젝트 리스트 조회 (List)
     */
    @Operation(
            summary = "팀별 프로젝트 리스트 조회 (List by TeamName)",
            description = """
            [결론]
            특정 팀이 소유한 모든 프로젝트의 목록을 반환합니다.
            
            [사용 화면]
            - 팀 메인 대시보드 화면
            
            [로직 설명]
            1. 팀 이름(teamName)으로 TeamEntity를 찾습니다.
            2. 해당 팀에 속한 모든 프로젝트를 조회합니다.
            3. 각 프로젝트별로 진행률(%)을 계산하여 리스트 형태로 반환합니다.
            
            [참조 테이블]
            - SELECT: team, project, todo
            """
    )
    @GetMapping("/project-list/by-team-name")
    public List<ProjectResponse> getProjectsByTeamName(@RequestParam("teamName") String teamName) {
        return projectService.getProjectsByTeamName(teamName);
    }
}