package com.TeamAA.TeamDo.controller;

import com.TeamAA.TeamDo.controller.Team.TeamController;
import com.TeamAA.TeamDo.dto.Team.MemberResponse;
import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.service.Team.TeamService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamControllerIntegrationTest {

    @Mock
    private TeamService teamService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private TeamController teamController;

    private final String userId = "leader";
    private TeamResponse sampleTeam;

    @BeforeEach
    void setUp() {
        sampleTeam = new TeamResponse(
                1L,
                "TeamA",
                "INVITE123",
                List.of(
                        new MemberResponse("leader", "Leader Name"),
                        new MemberResponse("member1", "Member Name")
                )
        );
    }

    // ---------------------------
    // 팀 생성
    // ---------------------------
    @Test
    void 팀생성_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);
        when(teamService.createTeam(eq("TeamA"), eq(userId))).thenReturn(sampleTeam);

        ResponseEntity<?> response = teamController.createTeam("TeamA", session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleTeam, response.getBody());
    }

    // ---------------------------
    // 내 팀 목록 조회
    // ---------------------------
    @Test
    void 내팀목록조회_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);
        when(teamService.getMyTeams(userId)).thenReturn(List.of(sampleTeam));

        ResponseEntity<?> response = teamController.getMyTeams(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(List.of(sampleTeam), response.getBody());
    }

    // ---------------------------
    // 팀 상세 조회
    // ---------------------------
    @Test
    void 팀상세조회_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);
        when(teamService.getTeamDetailDto(1L)).thenReturn(sampleTeam);

        ResponseEntity<?> response = teamController.getTeamDetail(1L, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleTeam, response.getBody());
    }

    // ---------------------------
    // 초대코드로 팀 참가
    // ---------------------------
    @Test
    void 초대코드참가_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);

        // void 메서드 joinTeamByInviteCode mocking
        doAnswer(invocation -> null).when(teamService).joinTeamByInviteCode(userId, "INVITE123");

        ResponseEntity<?> response = teamController.joinTeam("INVITE123", session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("팀 참가 완료", response.getBody());

        verify(teamService, times(1)).joinTeamByInviteCode(userId, "INVITE123");
    }

    // ---------------------------
    // 초대코드 재발급
    // ---------------------------
    @Test
    void 초대코드재발급_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);
        when(teamService.regenerateInviteCode(1L, userId)).thenReturn("NEWCODE123");

        ResponseEntity<?> response = teamController.regenerateInviteCode(1L, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("새 초대코드 : NEWCODE123", response.getBody());
    }

    // ---------------------------
    // 팀 나가기 (팀원)
    // ---------------------------
    @Test
    void 팀나가기_성공() {
        when(session.getAttribute("userId")).thenReturn(userId);
        doAnswer(invocation -> null).when(teamService).leaveTeam(userId, 1L);

        ResponseEntity<?> response = teamController.leaveTeam(1L, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("팀에서 나갔습니다.", response.getBody());
    }

    // ---------------------------
    // 인증 실패 테스트
    // ---------------------------
    @Test
    void 인증없음_테스트() {
        when(session.getAttribute("userId")).thenReturn(null);

        assertEquals(401, teamController.createTeam("TeamA", session).getStatusCodeValue());
        assertEquals(401, teamController.getMyTeams(session).getStatusCodeValue());
        assertEquals(401, teamController.getTeamDetail(1L, session).getStatusCodeValue());
        assertEquals(401, teamController.joinTeam("INVITE123", session).getStatusCodeValue());
        assertEquals(401, teamController.regenerateInviteCode(1L, session).getStatusCodeValue());
        assertEquals(401, teamController.leaveTeam(1L, session).getStatusCodeValue());
    }
}