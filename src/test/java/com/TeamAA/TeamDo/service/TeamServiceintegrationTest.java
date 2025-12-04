package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Team.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import com.TeamAA.TeamDo.service.Team.TeamService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TeamServiceintegrationTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamParticipatingRepository teamParticipatingRepository;

    private UserEntity leader;
    private UserEntity member;

    @BeforeEach
    void setUp() {
        leader = new UserEntity("leader", "leader@example.com", "pass", "Leader", false);
        member = new UserEntity("member", "member@example.com", "pass", "Member", false);
        userRepository.save(leader);
        userRepository.save(member);
    }

    // -----------------------------
    // 정상 기능 테스트
    // -----------------------------

    @Test
    void 팀_생성_및_조회() {
        TeamResponse team = teamService.createTeam("TestTeam", leader.getId());
        assertNotNull(team.id());
        assertEquals("TestTeam", team.name());
        assertEquals(1, team.participants().size());
        assertEquals(leader.getId(), team.participants().get(0).userId());

        List<TeamResponse> myTeams = teamService.getMyTeams(leader.getId());
        assertEquals(1, myTeams.size());
        assertEquals(team.id(), myTeams.get(0).id());
    }

    @Test
    void 팀_참가_및_상세조회() {
        TeamResponse team = teamService.createTeam("JoinTeam", leader.getId());
        teamService.joinTeamByInviteCode(member.getId(), team.inviteCode());
        TeamResponse detail = teamService.getTeamDetailDto(team.id());
        assertEquals(2, detail.participants().size());
    }

    @Test
    void 팀_나가기() {
        TeamResponse team = teamService.createTeam("LeaveTeam", leader.getId());
        teamService.joinTeamByInviteCode(member.getId(), team.inviteCode());

        teamService.leaveTeam(member.getId(), team.id());

        TeamResponse detail = teamService.getTeamDetailDto(team.id());
        assertEquals(1, detail.participants().size());
        assertEquals(leader.getId(), detail.participants().get(0).userId());
    }

    @Test
    void 초대코드_재발급() {
        TeamResponse team = teamService.createTeam("InviteCodeTeam", leader.getId());
        String oldCode = team.inviteCode();
        String newCode = teamService.regenerateInviteCode(team.id());
        assertNotNull(newCode);
        assertNotEquals(oldCode, newCode);

        teamService.joinTeamByInviteCode(member.getId(), newCode);
        TeamResponse detail = teamService.getTeamDetailDto(team.id());
        assertEquals(2, detail.participants().size());
    }

    @Test
    void 내_팀_목록_조회() {
        TeamResponse team1 = teamService.createTeam("TeamA", leader.getId());
        TeamResponse team2 = teamService.createTeam("TeamB", leader.getId());

        teamService.joinTeamByInviteCode(member.getId(), team1.inviteCode());

        List<TeamResponse> leaderTeams = teamService.getMyTeams(leader.getId());
        assertEquals(2, leaderTeams.size());

        List<TeamResponse> memberTeams = teamService.getMyTeams(member.getId());
        assertEquals(1, memberTeams.size());
        assertEquals(team1.id(), memberTeams.get(0).id());
    }

    // -----------------------------
    // 예외 테스트
    // -----------------------------

    @Test
    void 존재하지_않는_유저로_팀_생성시_예외() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.createTeam("NoUserTeam", "invalidUser"));

        assertEquals("유저를 찾을 수 없습니다.", ex.getMessage());
    }

    @Test
    void 존재하지_않는_초대코드로_팀_참가시_예외() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.joinTeamByInviteCode(member.getId(), "INVALID"));

        assertEquals("유효하지 않은 초대코드입니다.", ex.getMessage());
    }

    @Test
    void 이미_참가한_팀에_재참가하면_예외() {
        TeamResponse team = teamService.createTeam("TestTeam", leader.getId());
        teamService.joinTeamByInviteCode(member.getId(), team.inviteCode());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.joinTeamByInviteCode(member.getId(), team.inviteCode()));

        assertEquals("이미 팀에 참여중입니다.", ex.getMessage());
    }

    @Test
    void 존재하지_않는_팀_상세조회시_예외() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.getTeamDetailDto(999L));

        assertEquals("팀을 찾을 수 없습니다.", ex.getMessage());
    }

    @Test
    void 팀장이_팀을_나가려하면_예외() {
        TeamResponse team = teamService.createTeam("LeaveErrorTeam", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.leaveTeam(leader.getId(), team.id()));

        assertEquals("팀장은 팀을 나갈 수 없습니다.", ex.getMessage());
    }

    @Test
    void 팀에_없는_유저가_나가려하면_예외() {
        TeamResponse team = teamService.createTeam("LeaveTeam", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.leaveTeam(member.getId(), team.id()));

        assertEquals("팀 참여 정보를 찾을 수 없습니다.", ex.getMessage());
    }

    @Test
    void 존재하지_않는_팀의_초대코드_재발급시_예외() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.regenerateInviteCode(999L));

        assertEquals("팀을 찾을 수 없습니다.", ex.getMessage());
    }
}
