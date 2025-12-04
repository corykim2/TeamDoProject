package com.TeamAA.TeamDo.controller.Team;

import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.service.Team.TeamService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // ✅ 팀 생성
    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestParam String name, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            TeamResponse team = teamService.createTeam(name, userId);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ 내 팀 목록 조회
    @GetMapping("/teams")
    public ResponseEntity<?> getMyTeams(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        List<TeamResponse> teams = teamService.getMyTeams(userId);
        return ResponseEntity.ok(teams);
    }

    // ✅ 팀 상세 조회 (서비스에서 권한 체크)
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<?> getTeamDetail(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            TeamResponse teamDto = teamService.getTeamDetailDto(teamId);
            return ResponseEntity.ok(teamDto);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.equals("팀을 찾을 수 없습니다.")) return ResponseEntity.status(404).body(msg);
            if (msg.equals("팀원만 조회 가능합니다.")) return ResponseEntity.status(403).body(msg);
            return ResponseEntity.badRequest().body(msg);
        }
    }

    // ✅ 초대코드로 팀 참가
    @PostMapping("/teams/join/by-invite-code")
    public ResponseEntity<?> joinTeam(@RequestParam String inviteCode, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            teamService.joinTeamByInviteCode(userId, inviteCode);
            return ResponseEntity.ok("팀 참가 완료");
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.equals("이미 팀에 참여중입니다.")) return ResponseEntity.status(409).body(msg);
            return ResponseEntity.badRequest().body(msg);
        }
    }

    // ✅ 초대코드 재발급 (서비스에서 권한 체크)
    @PutMapping("/teams/{teamId}/invite-code")
    public ResponseEntity<?> regenerateInviteCode(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            String newCode = teamService.regenerateInviteCode(teamId, userId);
            return ResponseEntity.ok("새 초대코드 : " + newCode);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.equals("팀을 찾을 수 없습니다.")) return ResponseEntity.status(404).body(msg);
            if (msg.equals("팀장이 아니면 초대코드를 재발급할 수 없습니다.")) return ResponseEntity.status(403).body(msg);
            return ResponseEntity.badRequest().body(msg);
        }
    }

    // ✅ 팀 나가기 (서비스에서 팀장 여부 체크)
    @DeleteMapping("/teams/{teamId}/leave")
    public ResponseEntity<?> leaveTeam(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            teamService.leaveTeam(userId, teamId);
            return ResponseEntity.ok("팀에서 나갔습니다.");
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.equals("팀장은 팀을 나갈 수 없습니다.")) return ResponseEntity.status(403).body(msg);
            if (msg.equals("팀 참여 정보를 찾을 수 없습니다.")) return ResponseEntity.status(404).body(msg);
            return ResponseEntity.badRequest().body(msg);
        }
    }
}
