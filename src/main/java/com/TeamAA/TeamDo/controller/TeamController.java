package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.dto.CreateTeamRequest;
import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.TeamParticipatingEntity;
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

    // 팀 상세정보 조회
    @GetMapping("/{id}/details")
    public TeamEntity getTeamDetails(@PathVariable Long id) {
        return teamService.getTeamWithMembers(id);
    }

    // 팀원 초대
    @PostMapping("/{id}/members")
    public TeamParticipatingEntity addMember(@PathVariable Long id, @RequestParam String userId) {
        return teamService.addMemberToTeam(userId, id);
    }

    // 팀 나가기
    @DeleteMapping("/{id}/members")
    public void leaveTeam(@PathVariable Long id, @RequestParam String userId) {
        teamService.leaveTeam(userId, id);
    }
}
