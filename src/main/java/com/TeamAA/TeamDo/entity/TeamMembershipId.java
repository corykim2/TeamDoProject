package com.TeamAA.TeamDo.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMembershipId implements Serializable {

    private Integer team;
    private Integer user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMembershipId)) return false;
        TeamMembershipId that = (TeamMembershipId) o;
        return Objects.equals(team, that.team) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, user);
    }
}