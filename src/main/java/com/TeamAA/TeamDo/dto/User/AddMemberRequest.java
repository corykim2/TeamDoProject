package com.TeamAA.TeamDo.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequest {
    private String userId;

    @Getter
    @Setter
    public static class CreateTeamRequest {
        private String name;
    }
}
