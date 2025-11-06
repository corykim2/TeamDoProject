package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.AddMemberRequest;
import com.TeamAA.TeamDo.dto.CreateTeamRequest;
import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
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
    public TeamEntity createTeam(@RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request.getName());
    }

    // 팀 조회
    @GetMapping("/{id}")
    public TeamEntity getTeam(@PathVariable Long id) {
        return teamService.getTeam(id);
    }

    // 팀원 추가
    /*
    @PostMapping("/{teamId}/members")
    public UserEntity addMember(@PathVariable Long teamId, @RequestBody AddMemberRequest request) {
        return teamService.addMemberToTeam(request.getUserId(), teamId);
    }
     */
}
