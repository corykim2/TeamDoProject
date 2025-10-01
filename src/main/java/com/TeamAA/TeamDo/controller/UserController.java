package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.TeamAA.TeamDo.entity.User;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 폼 페이지 이동
    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup"; // signup.html 렌더링
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {
        try {
            userService.signUp(user);
            return "redirect:/success"; // 성공 시 이동
        } catch (IllegalArgumentException e) {
            return "redirect:/signup?error"; // 실패 시 에러 표시
        }
    }

    // 회원가입 성공 페이지
    @GetMapping("/success")
    public String success() {
        return "success"; // success.html
    }

    // 아이디로 유저 조회 (API)
    @GetMapping("/{id}")
    @ResponseBody
    public User getUser(@PathVariable String id) {
        return userService.findById(id); // 나중에 findById 구현 필요
    }
}
