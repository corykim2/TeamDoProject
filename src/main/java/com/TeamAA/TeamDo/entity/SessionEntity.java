package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "session")
public class SessionEntity {

    @Id
    @Column(name = "session_id", length = 64, nullable = false, updatable = false)
    private String sessionId; // PK

    // User 엔티티의 user_id(FK) 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity; // FK

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt; // 세션 생성 시간

    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt; // 세션 만료 시간

    // 기본 생성자
    public SessionEntity() {}

    // 전체 생성자
    public SessionEntity(String sessionId, UserEntity userEntity, Timestamp createdAt, Timestamp expiresAt) {
        this.sessionId = sessionId;
        this.userEntity = userEntity;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
