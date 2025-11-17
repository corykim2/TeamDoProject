package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pno")
    private Long pno; // 프로젝트번호 (PK)

    @CreationTimestamp
    @Column(updatable = false) //이 조건으로 업데이트시에는 아래거가 아래 옵션으로 insert 시에는 위에거가 사용되도록 함
    private LocalDateTime createdTime;

    @Column(name = "pname", nullable = false)
    private String pname; // 프로젝트명

    @ManyToOne(fetch = FetchType.LAZY) //정보가 필요할 때만 쿼리로 가져옴
    @JoinColumn(name = "team_code", nullable = false)
    private TeamEntity teamEntity; // 팀 코드

    @ManyToOne(fetch = FetchType.LAZY) //정보가 필요할 때만 쿼리로 가져옴
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity userEntity; // 팀 코드

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TodoEntity> TodoEntityList = new ArrayList<>();

    public void updateProject(String pname, TeamEntity teamEntity) { //프로젝트 정보를 수정하는 비즈니스 메서드
        this.pname = pname;
        this.teamEntity = teamEntity;
    }

}
