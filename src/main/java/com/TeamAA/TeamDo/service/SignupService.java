package com.TeamAA.TeamDo.service;

import com.TeamAA.TeamDo.controller.exceptionhandler.WithdrawnUserException;
import com.TeamAA.TeamDo.dto.SignupRequest;
import com.TeamAA.TeamDo.entity.UserEntity;
import com.TeamAA.TeamDo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity signup(SignupRequest request) {
            // 회원가입 예외처리

            // 입력데이터 누락 예외처리
            if (request.getId() == null || request.getId().isBlank()) {
                throw new IllegalArgumentException("아이디를 입력해주세요.");
            }

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("비밀번호를 입력해주세요.");
            }

            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new IllegalArgumentException("이메일을 입력해주세요.");
            }

            if (request.getName() == null || request.getName().isBlank()) {
                throw new IllegalArgumentException("이름을 입력해주세요.");
            }

            // 엔티티 범위초과 예외처리
            if (request.getId().length() < 6 || request.getId().length() >= 30) {
                throw new IllegalArgumentException("아이디는 6자리이상, 30자리 미만으로 입력해야합니다.");
            }

            if (request.getPassword().length() < 5 || request.getPassword().length() >= 16) {
                throw new IllegalArgumentException("비밀번호는 5자리이상, 16자리 미만으로 입력해야합니다.");
            }

            if (request.getEmail().length() >= 30) {
                throw new IllegalArgumentException("이메일은 30자리 미만으로 입력해야합니다.");
            }

            if (request.getName().length() >= 30) {
                throw new IllegalArgumentException("올바른 이름을 입력해주세요.");
            }
            // 중복 회원가입 아이디, 이메일 예외처리
            userRepository.findById(request.getId()).ifPresent(user -> {
                if (user.isWithdrawn()) {
                    // 탈퇴한 사용자
                    throw new WithdrawnUserException("탈퇴한 사용자 입니다. 탈퇴한 사용자는 재가입이 불가능합니다.");
                } else {
                    //중복아이디
                    throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
                }
            });

            //중복 이메일
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }


            // 회원가입 정상로직
        try {
            // 비밀번호 해싱
            String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

            UserEntity user = new UserEntity();
            user.setId(request.getId());
            user.setEmail(request.getEmail());
            user.setPassword(hashedPassword); // 해싱된 비밀번호 저장
            user.setName(request.getName());
            user.setWithdrawn(false);

            return userRepository.save(user);
        } catch(WithdrawnUserException e) {
            throw e;

        } catch (Exception e) {
            // 내부 서버 예외
            throw new RuntimeException("회원가입 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
        // 로그인용 유저 조회
        public UserEntity findById (String id){
            return userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못 되었습니다."));
        }
}