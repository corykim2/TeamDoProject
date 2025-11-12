package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.UserDeleteRequest;
import com.TeamAA.TeamDo.service.DeleteUserService;
import com.TeamAA.TeamDo.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserDeleteController {

    @Autowired
    private DeleteUserService deleteUserService;

    @PostMapping("/delete")
    public String deleteUser(
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody UserDeleteRequest request
    ) {
        deleteUserService.deleteUser(sessionId, request.getPassword());
        return "회원 탈퇴가 완료되었습니다.";
    }
}