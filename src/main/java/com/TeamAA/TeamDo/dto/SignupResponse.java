package com.TeamAA.TeamDo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponse {
    private String userId;
    private String message;

    public SignupResponse(String userId, String message){
        this.userId = userId;
        this.message = message;
    }
}
