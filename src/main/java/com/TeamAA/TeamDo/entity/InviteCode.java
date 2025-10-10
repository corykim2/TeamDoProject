package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invite_code")
public class InviteCode {

    @Id
    @Column(name = "invite_id")
    private Integer inviteId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String state;

    @ManyToOne
    @JoinColumn(name = "p_no")
    private Project project;
}