package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.service.SessionService;
import com.TeamAA.TeamDo.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@Tag(name = "Team API", description = "팀 생성, 초대코드, 팀 참가, 팀 정보 조회")
public class TeamController {

    private final TeamService teamService;
    private final SessionService sessionService;

    public TeamController(TeamService teamService, SessionService sessionService) {
        this.teamService = teamService;
        this.sessionService = sessionService;
    }

    // ✅ 팀 생성 + 생성자 자동참여
    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다. 팀 생성자는 자동으로 팀에 참여합니다.")
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(
            @RequestParam String name,
            HttpSession session
    ) {
        String userId = sessionService.getUserId(session);
        return ResponseEntity.ok(teamService.createTeam(name, userId));
    }

    // ✅ 초대코드로 팀 참가
    @Operation(summary = "초대코드로 팀 참가", description = "초대코드를 사용하여 팀에 참여합니다.")
    @PostMapping("/join")
    public ResponseEntity<?> joinTeam(
            @RequestParam String inviteCode,
            HttpSession session
    ) {
        String userId = sessionService.getUserId(session);
        return ResponseEntity.ok(teamService.joinTeamByInviteCode(userId, inviteCode));
    }

    // ✅ 팀 상세 조회
    @Operation(summary = "팀 상세 정보 조회", description = "팀 ID로 팀의 정보를 조회합니다.")
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamDetail(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamDetail(teamId));
    }

    // ✅ 초대코드 재발급
    @Operation(summary = "초대코드 재발급", description = "해당 팀의 초대코드를 새로운 값으로 재발급합니다.")
    @PostMapping("/{teamId}/invite/regenerate")
    public ResponseEntity<?> regenerateInviteCode(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.regenerateInviteCode(teamId));
    }

    // ✅ 팀 나가기
    @Operation(summary = "팀 나가기", description = "현재 로그인한 유저가 팀을 나갑니다.")
    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<?> leaveTeam(
            @PathVariable Long teamId,
            HttpSession session
    ) {
        String userId = sessionService.getUserId(session);
        teamService.leaveTeam(userId, teamId);
        return ResponseEntity.ok("팀에서 성공적으로 나갔습니다.");
    }
}
