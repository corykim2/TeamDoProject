package Team;

import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.service.Team.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    // ✅ 팀 생성 (생성자가 팀장)
    @Operation(
            summary = "팀 생성",
            description = """
            [결론]
            새로운 팀을 생성하며, 팀 생성자는 자동으로 팀장(Leader) 역할을 갖습니다.
            생성된 팀은 고유한 초대코드를 포함합니다.
        
            [사용 화면]
            - 마이페이지 > 팀 생성 버튼
        
            [로직 설명]
            1. 요청 DTO(팀 이름 등)를 검증합니다.
            2. 로그인된 사용자를 팀장으로 설정합니다.
            3. 팀 엔티티를 저장하며 고유 초대코드를 생성합니다.
            4. team_participating 테이블에 팀장 참여 정보가 저장됩니다.
        
            [참조 테이블]
            - INSERT: team, team_participating
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            }
    )
    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestParam String name, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        return ResponseEntity.ok(teamService.createTeam(name, userId));
    }

    // ✅ 내 팀 목록 조회
    @Operation(
            summary = "내 팀 목록 조회",
            description = """
            [결론]
            현재 로그인한 사용자가 속한 모든 팀 목록을 조회합니다.
        
            [사용 화면]
            - 마이페이지 > 팀 목록 조회 버튼
        
            [로직 설명]
            1. 로그인된 사용자의 ID로 팀 참여 테이블(team_participating)을 조회합니다.
            2. 사용자가 속한 모든 팀 정보를 반환합니다.
        
            [참조 테이블]
            - SELECT: team_participating, team
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            }
    )
    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getMyTeams(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(null);
        return ResponseEntity.ok(teamService.getMyTeams(userId));
    }

    // ✅ 팀 상세 조회 (팀원만 가능)
    @Operation(
            summary = "팀 상세 조회",
            description = """
    [결론]
     팀원만 팀의 상세 정보를 조회할 수 있습니다.

    [사용 화면]
    - 팀 페이지 > 상세 조회 버튼

    [로직 설명]
    1. 요청한 사용자가 해당 팀의 팀원인지 확인합니다.
    2. 팀 정보를 조회하여 DTO로 반환합니다.
    3. 팀원이 아닌 경우 접근 거부 처리합니다.
    
    [참조 테이블]
    - SELECT: team, team_participating, user
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 상세 조회 성공"),
                    @ApiResponse(responseCode = "403", description = "팀원만 조회 가능"),
                    @ApiResponse(responseCode = "404", description = "해당 팀 없음")
            }
    )
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<?> getTeamDetail(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        TeamResponse teamDto = teamService.getTeamDetailDto(teamId);
        boolean isMember = teamDto.participants().stream().anyMatch(p -> p.userId().equals(userId));
        if (!isMember) return ResponseEntity.status(403).body("팀 멤버만 접근 가능합니다.");

        return ResponseEntity.ok(teamDto);
    }

    // ✅ 초대코드로 팀 참가
    @Operation(
            summary = "초대코드로 팀 참가",
            description = """
    [결론]
    유효한 초대코드를 입력하면 해당 팀에 참여할 수 있습니다.

    [사용 화면]
    - 마이페이지 > 초대코드 입력 후 팀 참가버튼

    [로직 설명]
    1. 초대코드로 팀을 조회합니다.
    2. 로그인된 사용자가 이미 해당 팀에 참여 중인지 검증합니다.
    3. 참여 중이 아니면 team_participating 테이블에 데이터를 저장합니다.

    [참조 테이블]
    - SELECT: team
    - INSERT: team_participating
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 참가 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 또는 만료된 초대코드"),
                    @ApiResponse(responseCode = "409", description = "이미 팀에 참여 중")
            }
    )
    @PostMapping("/teams/join/by-invite-code")
    public ResponseEntity<?> joinTeam(@RequestParam String inviteCode, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            TeamParticipatingEntity participation = teamService.joinTeamByInviteCode(userId, inviteCode);
            return ResponseEntity.ok("팀 참가 완료");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ 초대코드 재발급 (팀장만 가능)
    @Operation(
            summary = "초대코드 재발급 (팀장 전용)",
            description = """
    [결론]
    팀장은 새로운 초대코드를 재발급할 수 있으며,
    기존 초대코드는 즉시 무효화됩니다.

    [사용 화면]
    - 팀 상세 페이지 > 초대코드 재발급 버튼

    [로직 설명]
    1. 로그인 사용자가 해당 팀의 팀장인지 검증합니다.
    2. 새로운 랜덤 초대코드를 생성합니다.
    3. 팀 테이블의 초대코드를 갱신합니다.

    [참조 테이블]
    - UPDATE: team
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "초대코드 재발급 성공"),
                    @ApiResponse(responseCode = "403", description = "팀장만 사용 가능한 기능"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 팀")
            }
    )
    @PutMapping("/teams/{teamId}/invite-code")
    public ResponseEntity<?> regenerateInviteCode(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        TeamEntity team = teamService.getTeamDetail(teamId);
        String leaderId = team.getParticipants().get(0).getUserEntity().getId();
        if (!leaderId.equals(userId)) return ResponseEntity.status(403).body("팀장만 초대코드를 재발급할 수 있습니다.");

        String newCode = teamService.regenerateInviteCode(teamId);
        return ResponseEntity.ok("새 초대코드 : " + newCode);
    }

    // ✅ 팀 나가기 (팀장은 불가능)
    @Operation(
            summary = "팀 나가기(팀장은 불가능)",
            description = """
    [결론]
    팀원은 팀을 자유롭게 나갈 수 있지만,
    팀장은 팀을 나갈 수 없습니다.

    [사용 화면]
    - 팀 상세 페이지 > 팀 나가기 버튼

    [로직 설명]
    1. 로그인 사용자가 해당 팀의 팀장인지 먼저 검증합니다.
    2. 팀장이면 탈퇴가 불가능합니다.
    3. 팀원일 경우 team_participating 레코드를 삭제합니다.

    [참조 테이블]
    - DELETE: team_participating
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 나가기 성공"),
                    @ApiResponse(responseCode = "403", description = "팀장은 팀을 나갈 수 없음"),
                    @ApiResponse(responseCode = "404", description = "해당 팀에 속해 있지 않음")
            }
    )
    @DeleteMapping("/teams/{teamId}/leave")
    public ResponseEntity<?> leaveTeam(@PathVariable Long teamId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        TeamEntity team = teamService.getTeamDetail(teamId);
        String leaderId = team.getParticipants().get(0).getUserEntity().getId();
        if (leaderId.equals(userId)) return ResponseEntity.badRequest().body("팀장은 팀을 나갈 수 없습니다. 팀을 삭제해야 합니다.");

        teamService.leaveTeam(userId, teamId);
        return ResponseEntity.ok("팀에서 나갔습니다.");
    }
}
