package com.TeamAA.TeamDo.repository.User;

import com.TeamAA.TeamDo.entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(String id);
    Optional<UserEntity> findByEmail(String email);
}