package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.LoginRequest;
import com.TeamAA.TeamDo.dto.LoginResponse;
import com.TeamAA.TeamDo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
