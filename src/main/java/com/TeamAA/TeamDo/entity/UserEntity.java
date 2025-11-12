package com.TeamAA.TeamDo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // 이메일 유니크 제약
})
public class UserEntity {

    @Id
    @Column(length = 30, nullable = false, updatable = false)
    private String id; // PK

    @Column(length = 30, nullable = false, unique = true)
    private String email; // 이메일

    @Column(length = 16, nullable = false)
    private String password; // 비밀번호

    @Column(length = 20, nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private boolean withdrawn = false; // 회원 탈퇴 여부, 기본 false

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TodoEntity> TodoEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectEntity> projectEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamParticipatingEntity> teamParticipatingEntityList = new ArrayList<>();

    // 기본 생성자
    public UserEntity() {}

    // 전체 생성자
    public UserEntity(String id, String email, String password, String name, boolean withdrawn) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.withdrawn = withdrawn;
    }
}
