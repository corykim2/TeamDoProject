package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @Column(name = "session_id", length = 64, nullable = false, updatable = false)
    private String sessionId; // PK

    // User 엔티티의 user_id(FK) 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // FK

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt; // 세션 생성 시간

    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt; // 세션 만료 시간

    // 기본 생성자
    public Session() {}

    // 전체 생성자
    public Session(String sessionId, User user, Timestamp createdAt, Timestamp expiresAt) {
        this.sessionId = sessionId;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getter & Setter
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }
}
