package com.TeamAA.TeamDo.controller.Team;

import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.service.Team.TeamService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // 팀 생성 처리
    @PostMapping("/create")
    public String createTeam(
            @RequestParam String name,
            HttpSession session,
            Model model
    ) {
        // 세션에서 로그인한 사용자 ID 가져오기
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "error";
        }

        // 팀 생성
        TeamEntity teamEntity = teamService.createTeam(name, userId);

        // 팀 정보를 뷰에 전달
        model.addAttribute("team", teamEntity);

        return "team-success"; // 생성 후 성공 페이지
    }
}
