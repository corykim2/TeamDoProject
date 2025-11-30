package com.TeamAA.TeamDo.service.User;

import com.TeamAA.TeamDo.controller.exceptionhandler.DuplicateException;
import com.TeamAA.TeamDo.controller.exceptionhandler.InvalidCredentialsException;
import com.TeamAA.TeamDo.controller.exceptionhandler.WithdrawnUserException;
import com.TeamAA.TeamDo.dto.User.SignupRequest;
import com.TeamAA.TeamDo.entity.User.UserEntity;
import com.TeamAA.TeamDo.repository.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity signup(SignupRequest request) {

        // 아이디 중복 + 탈퇴 회원 체크
        userRepository.findById(request.getId()).ifPresent(user -> {
            if (user.isWithdrawn()) {
                throw new WithdrawnUserException("탈퇴한 사용자 입니다. 탈퇴한 사용자는 재가입이 불가능합니다.");
            }
            throw new DuplicateException("이미 존재하는 아이디입니다.");
        });

        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("이미 존재하는 이메일입니다.");
        }

        try {
            //정상 처리
            String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

            UserEntity user = new UserEntity();
            user.setId(request.getId());
            user.setEmail(request.getEmail());
            user.setPassword(hashedPassword);
            user.setName(request.getName());
            user.setWithdrawn(false);

            return userRepository.save(user);
        //탈퇴한 유저 처리
        } catch (WithdrawnUserException | IllegalArgumentException e) {
            throw e;
        //내부 DB오류
        } catch (Exception e) {
            throw new RuntimeException("회원가입 처리 중 문제가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }

    public UserEntity findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못 되었습니다."));
    }
}
