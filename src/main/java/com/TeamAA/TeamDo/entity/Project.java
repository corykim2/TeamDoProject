package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_no")
    private Integer pNo;

    @Column(name = "p_name", nullable = false)
    private String pName;

    @Column(name = "progress_rate", nullable = false)
    private Double progressRate;

    @ManyToOne
    @JoinColumn(name = "team_code")
    private Team team;
}