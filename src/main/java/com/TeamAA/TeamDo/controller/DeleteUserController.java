package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.DeleteUserRequest;
import com.TeamAA.TeamDo.service.DeleteUserService;
import com.TeamAA.TeamDo.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class DeleteUserController {

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/delete")
    public String deleteUser(HttpSession session, @RequestBody DeleteUserRequest request) {

        String userId = sessionService.getUserId(session);
        deleteUserService.deleteUser(userId, request.getPassword());

        session.invalidate(); // 탈퇴 후 세션 종료
        return "회원 탈퇴가 완료되었습니다.";
    }
}