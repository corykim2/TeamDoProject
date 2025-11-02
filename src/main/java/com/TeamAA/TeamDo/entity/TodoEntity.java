package com.TeamAA.TeamDo.entity;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;

@Entity // 이 클래스가 JPA 엔티티임을 나타냅니다.
@Setter
@Table(name = "todo") // 매핑될 데이터베이스 테이블 이름을 지정합니다.
public class TodoEntity {

    @Id // 기본 키(Primary Key)를 지정합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략을 지정합니다. (AUTO_INCREMENT에 해당)
    @Column(name = "todoId")
    private Integer todoId; // 고유 식별자 (todo_id INT PRIMARY KEY AUTO_INCREMENT)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pNo", nullable = false)
    private Project project;


    @Column(name = "priority", nullable = false)
    private Integer priority; // priority INT NOT NULL

    @Column(name = "state", length = 64, nullable = false)
    private String state; // state VARCHAR(64) NOT NULL

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline; // deadline DATE NOT NULL (Java 8의 LocalDate 사용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId", nullable = false)
    private User managerId; // User 엔티티가 'users' 테이블에 매핑된다고 가정


    @Column(name = "name", length = 64, nullable = false)
    private String name; // 할 일 이름 (name VARCHAR(64) NOT NULL)


}