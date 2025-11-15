package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.DeleteUserRequest;
import com.TeamAA.TeamDo.dto.DeleteUserResponse;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.DeleteUserService;
import com.TeamAA.TeamDo.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class DeleteUserController {

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/delete")
    public ResponseEntity<DeleteUserResponse> deleteUser(@RequestBody DeleteUserRequest request, HttpSession session) {
        String userId = sessionService.getUserId(session); //세션검증
        UserEntity user = deleteUserService.deleteUser(userId, request);
        session.invalidate();
        DeleteUserResponse response = new DeleteUserResponse("회원 탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}