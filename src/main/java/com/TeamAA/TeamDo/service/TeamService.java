package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.TeamRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
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

    // ✅ 팀 생성
    public TeamEntity createTeam(String name) {
        TeamEntity team = new TeamEntity();
        team.setName(name);
        return teamRepository.save(team);
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

    // ✅ 팀 상세정보 조회
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
