package com.TeamAA.TeamDo.dto.Team;

import java.util.List;

public record TeamResponse(
        Long id,
        String name,
        String inviteCode,
        List<MemberResponse> participants
) {
    public String getInviteCode() {
        return "";
    }

    public Long getId() {
        return 0L;
    }
}