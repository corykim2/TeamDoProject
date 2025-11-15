package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        UserEntity user = signupService.signup(request);
        return "회원가입 성공: " + user.getId();
    }
}
