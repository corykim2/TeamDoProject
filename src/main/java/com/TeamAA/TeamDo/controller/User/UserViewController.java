package com.TeamAA.TeamDo.controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/auth")
public class UserViewController {


    @GetMapping("/login")
    public String loginPage() {
        return "login"; // → templates/login.html
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // → templates/signup.html
    }

}
