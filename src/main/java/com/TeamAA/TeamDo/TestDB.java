package com.TeamAA.TeamDo;

import com.TeamAA.TeamDo.entity.TestUser;
import com.TeamAA.TeamDo.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDB implements CommandLineRunner {

    private final TestUserRepository testUserRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== DB 연동 테스트 ===");

        // 데이터 삽입
        TestUser testUser = new TestUser();
        testUser.setUsername("kim");
        testUser.setPassword("1234");
        testUserRepository.save(testUser);
        System.out.println("User 저장 완료");

        // 전체 조회
        System.out.println("전체 User 수: " + testUserRepository.count());

        // 단건 조회
        TestUser findTestUser = testUserRepository.findByUsername("kim");
        System.out.println("조회된 User: " + findTestUser.getUsername() + ", " + findTestUser.getPassword());

        // 삭제
        testUserRepository.delete(findTestUser);
        System.out.println("User 삭제 완료");
        System.out.println("전체 User 수: " + testUserRepository.count());
    }
}
