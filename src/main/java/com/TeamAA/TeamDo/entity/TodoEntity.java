package com.TeamAA.TeamDo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity // 이 클래스가 JPA 엔티티임을 나타냅니다.
@Setter
@Getter
@Table(name = "todo") // 매핑될 데이터베이스 테이블 이름을 지정합니다.
public class TodoEntity {

    @Id // 기본 키(Primary Key)를 지정합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략을 지정합니다. (AUTO_INCREMENT에 해당)
    @Column(name = "todoId")
    private Long todoId; // 고유 식별자 (todo_id INT PRIMARY KEY AUTO_INCREMENT)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pNo", nullable = false)
    private ProjectEntity projectEntity;

    @CreationTimestamp
    @Column(updatable = false) //이 조건으로 업데이트시에는 아래거가 아래 옵션으로 insert 시에는 위에거가 사용되도록 함
    private LocalDateTime createdTime;

    @Column(name = "priority", nullable = false)
    @Min(1)
    @Max(5)
    private Integer priority; // priority INT NOT NULL

    @Column(name = "state", length = 64, nullable = false)
    private String state="미완"; // state VARCHAR(64) NOT NULL

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline; // deadline DATE NOT NULL (Java 8의 LocalDate 사용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerId", nullable = false)
    private UserEntity managerId;

    @Column(name = "name", length = 64, nullable = false)
    private String name; // 할 일 이름 (name VARCHAR(64) NOT NULL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creatorId", nullable = false)
    private UserEntity creatorId; // 생성자

}