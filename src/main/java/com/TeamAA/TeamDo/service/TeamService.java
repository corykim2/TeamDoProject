package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.TeamEntity;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.TeamRepository;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    // 팀 생성
    public TeamEntity createTeam(String name) {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setName(name);
        return teamRepository.save(teamEntity);
    }

    // 팀 조회
    public TeamEntity getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    // 팀원 추가
    /*
    public UserEntity addMemberToTeam(String userId, Long teamId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        TeamEntity teamEntity = getTeam(teamId);
        userEntity.setTeamEntity(teamEntity);
        return userRepository.save(userEntity);
    }
    */
    //TODO 조민성 이거 나중에 TeamParticipatingEntity로 들어가게 고치셈. 뭔가 잘못됨.
}
