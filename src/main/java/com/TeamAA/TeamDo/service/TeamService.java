package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.TeamRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 팀 생성
    public TeamEntity createTeam(String name) {
        TeamEntity team = new TeamEntity();
        team.setName(name);
        return teamRepository.save(team);
    }

    // 팀 조회
    public TeamEntity getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    // 팀 상세정보 조회
    public TeamEntity getTeamWithMembers(Long teamId) {
        TeamEntity team = getTeam(teamId);
        team.getTeamParticipatingEntityList().size(); // LAZY 초기화
        return team;
    }

    // 팀원 초대
    public TeamParticipatingEntity addMemberToTeam(String userId, Long teamId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        TeamEntity team = getTeam(teamId);

        if (teamParticipatingRepository.existsByUserEntityAndTeamEntity(user, team)) {
            throw new RuntimeException("이미 팀에 참여중인 유저입니다.");
        }

        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setUserEntity(user);
        participation.setTeamEntity(team);

        return teamParticipatingRepository.save(participation);
    }

    // 팀 나가기
    public void leaveTeam(String userId, Long teamId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        TeamEntity team = getTeam(teamId);

        TeamParticipatingEntity participation = teamParticipatingRepository
                .findByUserEntityAndTeamEntity(user, team)
                .orElseThrow(() -> new RuntimeException("팀 참여 정보를 찾을 수 없습니다."));

        teamParticipatingRepository.delete(participation);
    }
}
