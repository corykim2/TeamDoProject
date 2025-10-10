package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_membership")
@IdClass(TeamMembership.class)
public class TeamMembership {

    @Id
    @ManyToOne
    @JoinColumn(name = "team_code")
    private Team team;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}