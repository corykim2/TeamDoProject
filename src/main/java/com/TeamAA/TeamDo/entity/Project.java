package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pno")
    private Integer pno; // 프로젝트번호 (PK)

    @Column(name = "pname", nullable = false)
    private String pname; // 프로젝트명

    @Column(name = "team_code", nullable = false)
    private String teamCode; // 팀 코드

}
