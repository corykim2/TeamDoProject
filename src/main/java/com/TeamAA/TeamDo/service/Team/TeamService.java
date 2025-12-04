// src/main/java/com/TeamAA/TeamDo/service/Team/TeamService.java
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamParticipatingRepository teamParticipatingRepository;
    private final UserRepository userRepository;

    // 팀 생성 + 팀장 참여
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

        return mapToDto(savedTeam);
    }

    // 초대코드로 팀 참가
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

    // 팀 상세 조회
    public TeamResponse getTeamDetailDto(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
        return mapToDto(team);
    }

    // 내 팀 목록 조회
    public List<TeamResponse> getMyTeams(String userId) {
        List<TeamEntity> teams = teamParticipatingRepository.findTeamsByUserEntity_Id(userId);
        return teams.stream().map(this::mapToDto).toList();
    }

    // 팀 나가기
    public void leaveTeam(String userId, Long teamId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        String leaderId = team.getParticipants().get(0).getUserEntity().getId();
        if (leaderId.equals(userId)) throw new RuntimeException("팀장은 팀을 나갈 수 없습니다.");

        teamParticipatingRepository.deleteByUserEntityAndTeamEntity(user, team);
    }

    // 초대코드 재발급
    public String regenerateInviteCode(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
        String newCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        team.setInviteCode(newCode);
        teamRepository.save(team);
        return newCode;
    }

    // DTO 매핑
    private TeamResponse mapToDto(TeamEntity team) {
        List<MemberResponse> members = team.getParticipants().stream()
                .map(tp -> new MemberResponse(tp.getUserEntity().getId(), tp.getUserEntity().getName()))
                .toList();
        return new TeamResponse(team.getId(), team.getName(), team.getInviteCode(), members);
    }

    public TeamEntity getTeamDetail(Long teamId) {

        return null;
    }
}

