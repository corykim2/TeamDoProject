package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.AddMemberRequest;
import com.TeamAA.TeamDo.dto.CreateTeamRequest;
import com.TeamAA.TeamDo.entity.Team;
import com.TeamAA.TeamDo.entity.User;
import com.TeamAA.TeamDo.service.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // 팀 생성
    @PostMapping
    public Team createTeam(@RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request.getName());
    }

    // 팀 조회
    @GetMapping("/{id}")
    public Team getTeam(@PathVariable Long id) {
        return teamService.getTeam(id);
    }

    // 팀원 추가
    @PostMapping("/{teamId}/members")
    public User addMember(@PathVariable Long teamId, @RequestBody AddMemberRequest request) {
        return teamService.addMemberToTeam(request.getUserId(), teamId);
    }
}
