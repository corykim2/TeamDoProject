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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamServiceintegrationTest {

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
    private UserEntity outsider;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        leader = new UserEntity();
        leader.setId("leader");
        leader.setName("팀장");
        leader.setEmail("leader@test.com");
        leader.setPassword("1234");
        leader.setWithdrawn(false);
        userRepository.save(leader);

        member = new UserEntity();
        member.setId("member");
        member.setName("팀원");
        member.setEmail("member@test.com");
        member.setPassword("1234");
        member.setWithdrawn(false);
        userRepository.save(member);

        outsider = new UserEntity();
        outsider.setId("outsider");
        outsider.setName("외부인");
        outsider.setEmail("outsider@test.com");
        outsider.setPassword("1234");
        outsider.setWithdrawn(false);
        userRepository.save(outsider);
    }

    // -----------------------------
    // 팀 생성 예외
    // -----------------------------
    @Test
    void 팀생성_유저없음() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.createTeam("TeamA", "없는유저"));
        assertEquals("유저를 찾을 수 없습니다.", ex.getMessage());
    }

    // -----------------------------
    // 초대코드 참가 예외
    // -----------------------------
    @Test
    void 초대코드_잘못된코드() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.joinTeamByInviteCode(member.getId(), "WRONG123"));
        assertEquals("유효하지 않은 초대코드입니다.", ex.getMessage());
    }

    @Test
    void 초대코드_이미팀참여중() {
        TeamResponse team = teamService.createTeam("TeamX", leader.getId());
        teamService.joinTeamByInviteCode(member.getId(), team.inviteCode());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.joinTeamByInviteCode(member.getId(), team.inviteCode()));
        assertEquals("이미 팀에 참여중입니다.", ex.getMessage());
    }

    // -----------------------------
    // 팀 상세조회 예외
    // -----------------------------
    @Test
    void 팀상세조회_팀없음() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.getTeamDetailDto(99999L));
        assertEquals("팀을 찾을 수 없습니다.", ex.getMessage());
    }

    @Test
    void 팀상세조회_팀원아님() {
        TeamResponse team = teamService.createTeam("TeamE", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    // member와 outsider는 아직 참여하지 않음 → outsider 조회 시 예외
                    teamService.getTeamDetailDto(team.id());
                });
        assertEquals("팀원만 조회 가능합니다.", ex.getMessage());
    }

    // -----------------------------
    // 초대코드 재발급 예외
    // -----------------------------
    @Test
    void 초대코드재발급_팀없음() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.regenerateInviteCode(99999L, leader.getId()));
        assertEquals("팀을 찾을 수 없습니다.", ex.getMessage());
    }

    @Test
    void 초대코드재발급_팀장아님() {
        TeamResponse team = teamService.createTeam("TeamD", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.regenerateInviteCode(team.id(), member.getId()));
        assertEquals("팀장이 아니면 초대코드를 재발급할 수 없습니다.", ex.getMessage());
    }

    // -----------------------------
    // 팀 나가기 예외
    // -----------------------------
    @Test
    void 팀나가기_팀장나가기불가() {
        TeamResponse team = teamService.createTeam("TeamB", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.leaveTeam(leader.getId(), team.id()));
        assertEquals("팀장은 팀을 나갈 수 없습니다.", ex.getMessage());
    }

    @Test
    void 팀나가기_팀원아님() {
        TeamResponse team = teamService.createTeam("TeamC", leader.getId());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.leaveTeam(outsider.getId(), team.id()));
        assertEquals("팀 참여 정보를 찾을 수 없습니다.", ex.getMessage());
    }
}
