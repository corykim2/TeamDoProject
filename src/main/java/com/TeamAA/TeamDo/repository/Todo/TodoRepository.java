package com.TeamAA.TeamDo.repository.Todo;

import com.TeamAA.TeamDo.entity.Todo.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    // JpaRepository<엔티티 클래스, 기본 키 타입>

    // ⭐️ 추가적인 조회 메소드가 필요하면 여기에 선언합니다.
    List<TodoEntity> findByProjectEntity_pno(Long pno);

    List<TodoEntity> findByProjectEntity_pnoAndStateNot(Long i, String 완료);

    //상태(state)와 마감일(deadline)로 할 일 찾기
    //List<TodoEntity> findByStateAndDeadlineBefore(String state, LocalDate deadline);

}