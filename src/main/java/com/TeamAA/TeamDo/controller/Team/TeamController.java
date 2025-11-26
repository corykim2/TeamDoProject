package com.TeamAA.TeamDo.controller.Team;

import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.service.TeamService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 팀 상세 조회
     * - 로그인 필요
     * - 해당 팀 멤버만 접근 가능
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamDetail(
            @PathVariable Long teamId,
            HttpSession session
    ) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        TeamEntity team = teamService.getTeamDetail(teamId);

        // 사용자가 팀 멤버인지 확인
        boolean isMember = team.getParticipants().stream()
                .anyMatch(p -> p.getUserEntity().getId().equals(userId));

        if (!isMember) {
            return ResponseEntity.status(403).body("해당 팀에 접근할 권한이 없습니다.");
        }

        return ResponseEntity.ok(team);
    }

    /**
     * 초대코드로 팀 참여
     * - 로그인 필요
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinTeam(
            @RequestParam String inviteCode,
            HttpSession session
    ) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            TeamParticipatingEntity participation = teamService.joinTeamByInviteCode(userId, inviteCode);
            return ResponseEntity.ok("팀 참여 완료: " + participation.getTeamEntity().getName());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
