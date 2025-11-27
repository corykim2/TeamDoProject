package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.ProjectCreateRequest;
import com.TeamAA.TeamDo.entity.ProjectEntity; // 1. 엔티티 임포트 (이름 변경됨)
import com.TeamAA.TeamDo.entity.TeamEntity;     // 2. Team 엔티티 임포트
import com.TeamAA.TeamDo.entity.TodoEntity;
import com.TeamAA.TeamDo.entity.UserEntity;     // 3. User 엔티티 임포트
import com.TeamAA.TeamDo.repository.ProjectRepository;
import com.TeamAA.TeamDo.repository.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.TeamRepository; // 4. Team 리포지토리 임포트
import com.TeamAA.TeamDo.repository.UserRepository; // 5. User 리포지토리 임포트
import com.TeamAA.TeamDo.dto.ProjectUpdateRequest;
import com.TeamAA.TeamDo.dto.ProjectResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // (참고) 쓰기 작업에는 트랜잭션 권장

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository; // 6. TeamRepository 주입
    private final UserRepository userRepository; // 7. UserRepository 주입
    private final TeamParticipatingRepository teamParticipatingRepository;//팀 참여 여부를 확인하기 위해 주입

    @Transactional // 8. 데이터를 생성/수정/삭제할 땐 @Transactional 권장
    public ProjectEntity createProject(ProjectCreateRequest dto) {

        // 9. DTO에서 받은 ID로 부모 엔티티들(Team, User)을 조회
        TeamEntity team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Team ID: " + dto.getTeamId()));

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + dto.getUserId()));

        // 10. 조회한 엔티티(객체)를 포함하여 ProjectEntity 생성
        ProjectEntity newProject = ProjectEntity.builder()
                .pname(dto.getPname())
                .teamEntity(team)   // [중요] 문자열이 아닌 TeamEntity 객체를 연결
                .userEntity(user)   // [중요] UserEntity 객체를 연결
                // createdTime은 @CreationTimestamp가 자동 생성
                // todoEntityList는 처음엔 비어있음
                .build();

        return projectRepository.save(newProject);
    }


     //ID로 프로젝트 조회

    public ProjectResponse getProjectByPno(Long pno) {
        ProjectEntity project = projectRepository.findById(pno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project PNO: " + pno));

        // [추가] 엔티티와 계산된 진행률을 넘겨서 Response DTO 생성
        return new ProjectResponse(project, calculateProgress(project));
    }


    @Transactional
    public void deleteProject(Long pno) {
        projectRepository.deleteById(pno);
    }

    //  팀 이름으로 목록 조회
    public List<ProjectResponse> getProjectsByTeamName(String teamName) {

        // 1. 엔티티 목록 조회
        List<ProjectEntity> projects = projectRepository.findByTeamEntityName(teamName);

        // [중요] 이 return 문을 아래 코드로 덮어쓰세요.
        return projects.stream()
                .map(project -> new ProjectResponse(project, calculateProgress(project)))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectEntity updateProject(Long pno, ProjectUpdateRequest dto) {

        // 1. 수정할 프로젝트 찾기
        ProjectEntity project = projectRepository.findById(pno)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트가 없습니다. id=" + pno));

        // 2. 변경할 팀 찾기 (DTO에 있는 teamId로 조회)
        // (만약 팀 변경이 없다면 기존 팀을 그대로 유지하는 로직을 넣을 수도 있습니다)
        TeamEntity newTeam = teamRepository.findById(dto.getTeamId()) // dto.getTeamId()는 Long 타입이어야 함 (TeamRepository 확인 필요)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 없습니다. id=" + dto.getTeamId()));

        // 3. 엔티티의 수정 메서드 호출
        project.updateProject(dto.getPname(), newTeam);

        return project; // @Transactional 덕분에 projectRepository.save()를 안 호출해도 자동 업데이트됨
    }

    private int calculateProgress(ProjectEntity project) {
        // 1. 이 프로젝트에 속한 To-Do 리스트 가져오기
        List<TodoEntity> todos = project.getTodoEntityList();

        // 2. 전체 To-Do가 0개이면 0% 반환
        if (todos == null || todos.isEmpty()) {
            return 0;
        }

        // 3. 완료된 To-Do 개수 세기 (String state 필드 기준)
        // [중요] "DONE"이 완료 상태를 의미한다고 가정합니다.추후 수정
        long completedCount = todos.stream()
                .filter(todo -> "DONE".equalsIgnoreCase(todo.getState()))
                .count();

        // 4. 백분율로 변환 후 정수(int)로 반환
        return (int) Math.round(((double) completedCount / todos.size()) * 100);
    }

    public void validateUserInProject(Long projectId, String userId) {

        // 1. 프로젝트 정보 가져오기 (어떤 팀인지 알아야 하니까)
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // 2. 프로젝트가 속한 팀의 ID 추출
        Long teamId = project.getTeamEntity().getId();

        // 3. 해당 팀에 유저가 참여 중인지 확인 (Repository 호출)
        boolean isMember = teamParticipatingRepository.existsByTeamEntity_IdAndUserEntity_Id(teamId, userId);

        // 4. 참여자가 아니라면 에러 발생 (권한 없음)
        if (!isMember) {
            throw new IllegalArgumentException("해당 프로젝트에 접근 권한이 없습니다. (팀 멤버가 아님)");
            // 나중에 security 적용시 AccessDeniedException 등을 사용하면 좋음
        }
    }
}