package com.TeamAA.TeamDo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // 이메일 유니크 제약
})
public class User {

    @Id
    @Column(length = 30, nullable = false, updatable = false)
    private String id; // PK

    @Column(length = 30, nullable = false, unique = true)
    private String email; // 이메일

    @Column(length = 16, nullable = false)
    private String password; // 비밀번호

    @Column(length = 20, nullable = false)
    private String name; // 이름

    @Column(length = 20)
    private String organization; // 소속기관 (nullable)

    @Column(nullable = false)
    private boolean withdrawn = false; // 회원 탈퇴 여부, 기본 false

    // 기본 생성자
    public User() {}

    // 전체 생성자
    public User(String id, String email, String password, String name, String organization, boolean withdrawn) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.organization = organization;
        this.withdrawn = withdrawn;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }
    public void setWithdrawn(boolean withdrawn) {
        this.withdrawn = withdrawn;
    }
}
