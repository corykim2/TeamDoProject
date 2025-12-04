// src/test/java/com/TeamAA/TeamDo/service/TeamServiceIntegrationTest.java
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
        // 팀 여러 개 생성
        TeamResponse team1 = teamService.createTeam("TeamA", leader.getId());
        TeamResponse team2 = teamService.createTeam("TeamB", leader.getId());

        // 멤버를 TeamA에만 참가시킴
        teamService.joinTeamByInviteCode(member.getId(), team1.inviteCode());

        List<TeamResponse> leaderTeams = teamService.getMyTeams(leader.getId());
        assertEquals(2, leaderTeams.size());

        List<TeamResponse> memberTeams = teamService.getMyTeams(member.getId());
        assertEquals(1, memberTeams.size());
        assertEquals(team1.id(), memberTeams.get(0).id());
    }
}
