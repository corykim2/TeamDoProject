package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.dto.SignupResponse;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        UserEntity user = signupService.signup(request);
        SignupResponse response = new SignupResponse(user.getId(), "회원가입 성공");
        return ResponseEntity.ok(response);
    }
}
