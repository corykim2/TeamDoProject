package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_code")
    private Integer teamCode;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;
}