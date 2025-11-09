package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "team")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdTime;

    @Column(length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "teamEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectEntity> projectEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "teamEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamParticipatingEntity> teamParticipatingEntityList = new ArrayList<>();
}
