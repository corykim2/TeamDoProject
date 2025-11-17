package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.ProjectCreateRequestDto;
import com.TeamAA.TeamDo.entity.ProjectEntity; // 1. 엔티티 임포트 (이름 변경됨)
import com.TeamAA.TeamDo.entity.TeamEntity;     // 2. Team 엔티티 임포트
import com.TeamAA.TeamDo.entity.UserEntity;     // 3. User 엔티티 임포트
import com.TeamAA.TeamDo.repository.ProjectRepository;
import com.TeamAA.TeamDo.repository.TeamRepository; // 4. Team 리포지토리 임포트
import com.TeamAA.TeamDo.repository.UserRepository; // 5. User 리포지토리 임포트
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


    @Transactional // 8. 데이터를 생성/수정/삭제할 땐 @Transactional 권장
    public ProjectEntity createProject(ProjectCreateRequestDto dto) {

        // 9. DTO에서 받은 ID로 부모 엔티티들(Team, User)을 조회
        TeamEntity team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Team ID: " + dto.getTeamId()));

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + dto.getUserId()));

        // 10. 조회한 엔티티(객체)를 포함하여 ProjectEntity 생성
        ProjectEntity newProject = ProjectEntity.builder()
                .pName(dto.getPname())
                .teamEntity(team)   // [중요] 문자열이 아닌 TeamEntity 객체를 연결
                .userEntity(user)   // [중요] UserEntity 객체를 연결
                // createdTime은 @CreationTimestamp가 자동 생성
                // todoEntityList는 처음엔 비어있음
                .build();

        return projectRepository.save(newProject);
    }


     //ID로 프로젝트 조회

    public ProjectEntity getProjectByPno(Integer pno) {
        return projectRepository.findById(pno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project PNO: " + pno));
    }


    @Transactional
    public void deleteProject(Integer pno) {
        projectRepository.deleteById(pno);
    }

    /**
     * 팀 코드로 프로젝트 리스트 조회 (Repository 수정 필요)
     * (이 기능은 이제 TeamEntity 내부를 탐색해야 하므로 더 복잡한 쿼리가 필요할 수 있습니다)
     */
    // ... getProjectsByTeamCode ... (일단 보류)
}