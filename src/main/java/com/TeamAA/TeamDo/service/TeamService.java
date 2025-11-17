package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Team.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamParticipatingRepository teamParticipatingRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository, TeamParticipatingRepository teamParticipatingRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamParticipatingRepository = teamParticipatingRepository;
    }

    // ✅ 팀 생성 + 생성자 자동 참여
    public TeamEntity createTeam(String name, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        TeamEntity team = new TeamEntity();
        team.setName(name);

        TeamEntity savedTeam = teamRepository.save(team);

        // 생성자 자동 참여
        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setUserEntity(user);
        participation.setTeamEntity(savedTeam);
        teamParticipatingRepository.save(participation);

        return savedTeam;
    }

    // ✅ 초대코드로 팀 참가
    public TeamParticipatingEntity joinTeamByInviteCode(String userId, String inviteCode) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        TeamEntity team = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 초대코드입니다."));

        boolean exists = teamParticipatingRepository.existsByUserEntityAndTeamEntity(user, team);
        if (exists) throw new RuntimeException("이미 팀에 참여중입니다.");

        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setUserEntity(user);
        participation.setTeamEntity(team);

        return teamParticipatingRepository.save(participation);
    }

    // ✅ 팀 상세 조회
    public TeamEntity getTeamDetail(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    // ✅ 초대코드 재발급
    public String regenerateInviteCode(Long teamId) {
        TeamEntity team = getTeamDetail(teamId);
        team.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        teamRepository.save(team);
        return team.getInviteCode();
    }

    // ✅ 팀 나가기
    public void leaveTeam(String userId, Long teamId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        TeamEntity team = getTeamDetail(teamId);

        teamParticipatingRepository.deleteByUserEntityAndTeamEntity(user, team);
    }
}
