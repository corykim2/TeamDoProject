package com.TeamAA.TeamDo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String sessionId;
    private String userId;
    private String message;

    public LoginResponse(String sessionId, String userId, String message) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.message = message;
    }
}