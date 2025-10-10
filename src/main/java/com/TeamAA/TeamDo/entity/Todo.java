package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @Column(name = "p_no")
    private Integer pNo;

    @OneToOne
    @JoinColumn(name = "p_no", insertable = false, updatable = false)
    private Project project;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private Date deadline;

    @Column(nullable = false)
    private String manager;

    @Column(nullable = false)
    private String name;
}