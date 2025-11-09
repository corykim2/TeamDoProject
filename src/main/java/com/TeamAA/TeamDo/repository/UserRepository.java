package com.TeamAA.TeamDo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.TeamAA.TeamDo.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    // 아이디 중복체크
    boolean existsById(String id);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    //아이디 확인
    Optional<UserEntity> findById(String id);

}