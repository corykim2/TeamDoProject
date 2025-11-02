package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.entity.Team;
import com.TeamAA.TeamDo.entity.User;
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
    public Team createTeam(String name) {
        Team team = new Team(name);
        return teamRepository.save(team);
    }

    // 팀 조회
    public Team getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    }

    // 팀원 추가
    public User addMemberToTeam(String userId, Long teamId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Team team = getTeam(teamId);
        user.setTeam(team);
        return userRepository.save(user);
    }
}
