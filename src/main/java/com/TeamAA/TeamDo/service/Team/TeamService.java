package com.TeamAA.TeamDo.service.Team;

import com.TeamAA.TeamDo.dto.Team.MemberResponse;
import com.TeamAA.TeamDo.dto.Team.TeamResponse;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;

import com.TeamAA.TeamDo.entity.Team.TeamParticipatingEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.Team.TeamParticipatingRepository;
import com.TeamAA.TeamDo.repository.Team.TeamRepository;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamParticipatingRepository teamParticipatingRepository;
    private final UserRepository userRepository;  // ✅ UserRepository 주입

    // 팀 상세 조회
    public TeamEntity getTeamDetail(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    // ✅ 팀 생성 + 생성자 자동 참여 (팀장)
    public TeamResponse createTeam(String name, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        TeamEntity team = new TeamEntity();
        team.setName(name);
        team.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        TeamEntity savedTeam = teamRepository.save(team);

        TeamParticipatingEntity participation = new TeamParticipatingEntity();
        participation.setUserEntity(user);
        participation.setTeamEntity(savedTeam);
        teamParticipatingRepository.save(participation);

        return new TeamResponse(
                savedTeam.getId(),
                savedTeam.getName(),
                savedTeam.getInviteCode(),
                List.of(new MemberResponse(user.getId(), user.getName()))
        );
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
        return (TeamParticipatingEntity) teamParticipatingRepository.save(participation);
    }

    // ✅ 팀 상세 조회 DTO 반환
    public TeamResponse getTeamDetailDto(Long teamId) {
        TeamEntity team = getTeamDetail(teamId);
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getInviteCode(),
                team.getParticipants().stream()
                        .map(p -> new MemberResponse(p.getUserEntity().getId(), p.getUserEntity().getName()))
                        .toList()
        );
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

    // ✅ 내가 속한 팀 목록 조회
    public List<TeamResponse> getMyTeams(String userId) {
        // ✅ JPQL로 유저가 속한 팀만 조회
        List<TeamEntity> teams = teamParticipatingRepository.findTeamsByUserEntity_Id(userId);

        return teams.stream().map(team -> new TeamResponse(
                team.getId(),
                team.getName(),
                team.getInviteCode(),
                team.getParticipants().stream()
                        .map(p -> new MemberResponse(p.getUserEntity().getId(), p.getUserEntity().getName()))
                        .toList()
        )).toList();
    }
}