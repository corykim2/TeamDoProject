package com.TeamAA.TeamDo.controller;

//이거 여기로 요청 넣으면 예시데이터 넣으려고 만드는 거에요

import com.TeamAA.TeamDo.dto.ProjectCreateRequest;
import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.dto.TodoCreateRequest;
import com.TeamAA.TeamDo.entity.Project.ProjectEntity;
import com.TeamAA.TeamDo.entity.Team.TeamEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.service.ProjectService;
import com.TeamAA.TeamDo.service.SignupService;
import com.TeamAA.TeamDo.service.TeamService;
import com.TeamAA.TeamDo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class InsertSampleController {
    private final SignupService signupService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final TodoService todoService;

    @GetMapping("/insert")
    public String regenerateInviteCode() {
        int userCount = 100; //몇명 만들 건지
        int memberCount = 5; //팀에 팀원 몇명 할 건지
        int teamCount = userCount-memberCount; //팀 몇 개 만들건지
        int todoCount = 10; //투두 몇 개씩 생성할 건지

        List<Long> teamIdList = new ArrayList<>();
        List<Integer> projectIdList = new ArrayList<>();
        List<UserEntity> userEntityList = new ArrayList<>();

        //1. 유저 먼저 생성
        for (int i = 1; i <= userCount; i++) {
            SignupRequest req = new SignupRequest();
            req.setId("user_" + i);
            req.setEmail("user_" + i + "@example.com");
            req.setPassword("pass_" + i);
            req.setName("예시_사용자" + i);
            userEntityList.add(signupService.signup(req));
        }
        //2. 각자 팀을 1개씩 만들고, 각 팀에 새로 4명씩 가입. -> 5명이 한 팀. 팀은
        for (int i = 1; i <= teamCount; i++) {
            TeamEntity teamEntity = teamService.createTeam("Team_" + i, "user_" + i);
            String inviteCode = teamEntity.getInviteCode();
            teamIdList.add(teamEntity.getId());
            for(int j = i+1; j < (i+5); j++) {
                teamService.joinTeamByInviteCode("user_" + j, inviteCode);
            }
        }
        System.out.println("팀 생성 완료");
        //3. 프로젝트를 각자 하나씩 만듦
        for (int i = 1; i <= teamCount; i++) {
            ProjectCreateRequest req = new ProjectCreateRequest();
            req.setPname("프로젝트_"+i);
            req.setUserId("user_" + i);
            req.setTeamId(teamIdList.get(i-1));
            ProjectEntity projectEntity = projectService.createProject(req);
            projectIdList.add(projectEntity.getPno());
        }
        System.out.println("프로젝트 생성 완료");


        //4. 각 프로젝트에 팀원이 투두 10개씩 생성
        for (int i = 1; i <= teamCount; i++) {
            for(int j = 0; j<memberCount; j++){
                for(int k = 0; k<todoCount; k++){
                    TodoCreateRequest req = new TodoCreateRequest();
                    req.setName(i+"번플젝" + j + "번 멤버가 할 일:" + k);
                    req.setDeadline(LocalDate.of(2025, 11, 17));
                    req.setManagerId("user_" + i);
                    req.setPriority(1);
                    req.setPNo(projectIdList.get(i-1));
                    todoService.createTodo(req, userEntityList.get(i));
                }
            }
        }

        return "샘플 데이터 삽입 완료!";
    }

    @GetMapping("/insert2")
    public String regenerateInviteCode2() {
        TodoCreateRequest req = new TodoCreateRequest();
        req.setName("sdsd");
        req.setDeadline(LocalDate.of(2025, 11, 17));
        req.setManagerId("user_1");
        req.setPriority(1);
        req.setPNo(1);

        todoService.createTodo(req, signupService.findById("user_1"));

        return "샘플 데이터 삽입 완료!";
    }
}
