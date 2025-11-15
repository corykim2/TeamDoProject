package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Integer> {
    // JpaRepository<엔티티 클래스, 기본 키 타입>

    // ⭐️ 추가적인 조회 메소드가 필요하면 여기에 선언합니다.
    List<TodoEntity> findByProjectEntity_pno(Integer pno);

    //상태(state)와 마감일(deadline)로 할 일 찾기
    //List<TodoEntity> findByStateAndDeadlineBefore(String state, LocalDate deadline);

}