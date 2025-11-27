package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.ProjectCreateRequest;
import com.TeamAA.TeamDo.dto.ProjectUpdateRequest;
import com.TeamAA.TeamDo.dto.ProjectResponse;
import com.TeamAA.TeamDo.entity.ProjectEntity; // 1. 엔티티 이름 변경 반영
import com.TeamAA.TeamDo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "프로젝트 API", description = "프로젝트 CRUD 및 진행률 조회") // [SWAGGER]  API 섹션 정의
@RestController // REST API 컨트롤러
@RequestMapping("/api/projects") // 공통 URL 경로
@RequiredArgsConstructor // 서비스 주입
public class ProjectController {

    private final ProjectService projectService;

    /**
     * API 1: 프로젝트 생성 (POST 방식)
     * URL: POST /api/projects
     * @RequestBody: 사용자가 보낸 JSON 데이터를 DTO 객체로 변환해 줌
     */
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.") // [SWAGGER] 3. API 설명 추가
    @PostMapping
    public ProjectEntity createProject(@RequestBody ProjectCreateRequest requestDto) {

        // 4. DTO에서 데이터를 꺼내 서비스로 전달
        return projectService.createProject(requestDto);
    }

    /**
     * API 2: 특정 프로젝트 조회 (GET 방식)
     * URL: GET /api/projects/1  (여기서 1이 pno)
     * @PathVariable: URL 경로에 포함된 값(pno)을 파라미터로 받아옴
     */
    @Operation(summary = "특정 프로젝트 조회", description = "pno(ID)로 프로젝트 1건과 진행률을 조회합니다.")
    @GetMapping("/{pno}")
    public ProjectResponse getProject(@PathVariable Long pno) {

        // 5. URL에서 받은 pno를 서비스로 전달
        return projectService.getProjectByPno(pno);
    }

    /**
     * API 3: 특정 프로젝트 삭제 (DELETE 방식)
     * URL: DELETE /api/projects/1
     */
    @Operation(summary = "프로젝트 삭제", description = "pno(ID)로 프로젝트 1건을 삭제합니다.")
    @DeleteMapping("/{pno}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long pno) {

        // 2. 서비스의 deleteProject 메서드 호출
        projectService.deleteProject(pno);

        // 3. 성공적으로 삭제되었음을 알리는 "200 OK" 빈 응답 반환
        return ResponseEntity.ok().build();
    }

    /**
     * API 4: 팀 이름으로 프로젝트 목록 조회 (GET 방식)
     *
     */
    @Operation(summary = "팀 이름으로 프로젝트 목록 조회", description = "팀 이름(name)으로 해당 팀의 프로젝트 목록과 진행률을 조회합니다.")
    @GetMapping
    public List<ProjectResponse> getProjectsByTeamName(
                                                        @RequestParam("teamName") String teamName) {

        return projectService.getProjectsByTeamName(teamName);
    }

    @Operation(summary = "프로젝트 수정", description = "pno(ID)로 프로젝트 1건의 이름(pname)과 소속 팀(teamId)을 수정합니다.")
    @PutMapping("/{pno}")
    public ProjectEntity updateProject(@PathVariable Long pno,
                                       @RequestBody ProjectUpdateRequest requestDto,
                                       @RequestParam String userId) {
        projectService.validateUserInProject(Long.valueOf(pno), userId);

        return projectService.updateProject(Long.valueOf(pno), requestDto);
    }
}
