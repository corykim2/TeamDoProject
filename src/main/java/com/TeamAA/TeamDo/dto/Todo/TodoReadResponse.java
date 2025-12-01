package com.TeamAA.TeamDo.dto.Todo;

import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import com.TeamAA.TeamDo.entity.User.UserEntity; // UserEntity 참조를 위해 필요
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TodoReadResponse {

    // 할 일 고유 ID (PK)
    private Long todoId;
    // 프로젝트 ID (FK)
    private Long pNo;
    // 할 일 이름
    private String name;
    // 담당자 ID
    private String managerId;
    // 생성자 ID
    private String creatorId;
    // 상태 (예: "미완", "완료")
    private String state;
    // 마감일
    private LocalDate deadline;
    // 우선순위
    private Integer priority;
    // 생성 시간
    private LocalDateTime createdTime;

    /**
     * TodoEntity를 TodoReadResponse DTO로 변환하는 정적 팩토리 메서드
     * 이 메서드는 Controller/Service 계층에서 엔티티를 클라이언트에게 보낼 DTO로 변환할 때 사용됩니다.
     */
    public static TodoReadResponse fromEntity(TodoEntity entity) {
        // null 체크를 포함하여 안전하게 UserEntity ID를 추출합니다.
        String managerId = (entity.getManagerId() != null) ? entity.getManagerId().getId() : null;
        String creatorId = (entity.getCreatorId() != null) ? entity.getCreatorId().getId() : null;

        return TodoReadResponse.builder()
                .todoId(entity.getTodoId())
                .pNo(entity.getProjectEntity().getPno())
                .name(entity.getName())
                .managerId(managerId)
                .creatorId(creatorId)
                .state(entity.getState())
                .deadline(entity.getDeadline())
                .priority(entity.getPriority())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}