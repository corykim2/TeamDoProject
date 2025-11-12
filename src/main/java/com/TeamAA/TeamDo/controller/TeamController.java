package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.TeamParticipatingEntity;
import com.TeamAA.TeamDo.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // ✅ 팀 생성
    @PostMapping("/create")
    public ResponseEntity<TeamEntity> createTeam(@RequestParam String name) {
        TeamEntity team = teamService.createTeam(name);
        return ResponseEntity.ok(team);
    }

    // ✅ 초대코드로 팀 참여
    @PostMapping("/join")
    public ResponseEntity<String> joinTeam(@RequestParam String userId, @RequestParam String inviteCode) {
        TeamParticipatingEntity result = teamService.joinTeamByInviteCode(userId, inviteCode);
        return ResponseEntity.ok("팀 참여 완료: " + result.getTeamEntity().getName());
    }

    // ✅ 팀 상세 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamEntity> getTeamDetail(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamDetail(teamId));
    }

    // ✅ 초대코드 재발급
    @PostMapping("/{teamId}/regenerate-code")
    public ResponseEntity<String> regenerateInviteCode(@PathVariable Long teamId) {
        String newCode = teamService.regenerateInviteCode(teamId);
        return ResponseEntity.ok("새 초대코드: " + newCode);
    }

    // ✅ 팀 나가기
    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveTeam(@RequestParam String userId, @RequestParam Long teamId) {
        teamService.leaveTeam(userId, teamId);
        return ResponseEntity.ok("팀에서 나갔습니다.");
    }
}
