package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.ProjectCreateRequestDto; // 1. DTO
import com.TeamAA.TeamDo.entity.Project;             // 2. 엔티티 (응답용)
import com.TeamAA.TeamDo.service.ProjectService;       // 3. 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    @PostMapping
    public Project createProject(@RequestBody ProjectCreateRequestDto requestDto) {

        // 4. DTO에서 데이터를 꺼내 서비스로 전달
        return projectService.createProject(requestDto);
    }

    /**
     * API 2: 특정 프로젝트 조회 (GET 방식)
     * URL: GET /api/projects/1  (여기서 1이 pno)
     * @PathVariable: URL 경로에 포함된 값(pno)을 파라미터로 받아옴
     */
    @GetMapping("/{pno}")
    public Project getProject(@PathVariable Integer pno) {

        // 5. URL에서 받은 pno를 서비스로 전달
        return projectService.getProjectByPno(pno);
    }
}
