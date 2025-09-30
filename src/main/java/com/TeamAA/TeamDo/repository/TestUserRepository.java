package com.TeamAA.TeamDo.repository;

import com.TeamAA.TeamDo.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {
    TestUser findByUsername(String username);
}
