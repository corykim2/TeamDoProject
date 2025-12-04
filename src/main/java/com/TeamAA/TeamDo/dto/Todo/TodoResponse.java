package com.TeamAA.TeamDo.dto.Todo;

import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TodoResponse {

    private Long todoId;
    private String name;
    private String state;
    private Integer priority;
    private LocalDate deadline;
    private Long projectId;
    private String managerId;

    // 엔티티 -> DTO 변환 생성자
    public TodoResponse(TodoEntity todo) {
        this.todoId = todo.getTodoId();
        this.name = todo.getName();
        this.state = todo.getState();
        this.priority = todo.getPriority();
        this.deadline = todo.getDeadline();
        // 프로젝트 ID 꺼내기
        this.projectId = todo.getProjectEntity().getPno();
        this.managerId = todo.getManagerId().getId();
    }
}