package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    // JpaRepository<엔티티 클래스, 기본 키 타입>

    List<TodoEntity> findByProjectEntity_pno(Integer pNo);

    List<TodoEntity> findByProjectEntity_pnoAndStateNot(int i, String 완료);

    //상태(state)와 마감일(deadline)로 할 일 찾기
    //List<TodoEntity> findByStateAndDeadlineBefore(String state, LocalDate deadline);

}