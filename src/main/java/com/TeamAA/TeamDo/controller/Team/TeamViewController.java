package com.TeamAA.TeamDo.controller.Team;

import com.TeamAA.TeamDo.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamViewController {

    private final TeamService teamService;

    // 팀 생성 폼 페이지
    @GetMapping("/create")
    public String showCreateForm() {
        return "create-team"; // resources/templates/create-team.html
    }

    //TODO 민성님 여기 create 2개라서 오류나는 거니까 나중에 고치십셔
    /*
    // 팀 생성 처리
    @PostMapping("/create")
    public String createTeam(@RequestParam String name, Model model) {
        TeamEntity teamEntity = teamService.createTeam(name);
        model.addAttribute("team", teamEntity);
        return "team-success"; // 생성 후 성공 페이지
    }

     */
}
