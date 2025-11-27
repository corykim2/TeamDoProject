package com.TeamAA.TeamDo.controller.exceptionhandler;

import com.TeamAA.TeamDo.dto.User.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class    GlobalAuthExceptionHandler {

    //아이디, 이메일 중복 처리
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(InvalidCredentialsException e) {
        ErrorResponse error = new ErrorResponse(409,e.getMessage());
        return ResponseEntity.status(409).body(error);
    }

    //아이디,비밀번호 불일치 처리
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        ErrorResponse error = new ErrorResponse(401,e.getMessage());
        return ResponseEntity.status(401).body(error);
    }

    //Bad Request처리(입력값 중복, 입력값 누락, 데이터 범위 초과)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse(400,e.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    //탈퇴한 유저
    @ExceptionHandler(WithdrawnUserException.class)
    public ResponseEntity<ErrorResponse> handleWithdrawnUser(WithdrawnUserException e) {
        ErrorResponse error = new ErrorResponse(403, e.getMessage());
        return ResponseEntity.status(403).body(error);
    }

    // 서버내부 오류
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleServerError(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(500, e.getMessage());
        return ResponseEntity.status(500).body(error);
    }

    // DTO 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        ErrorResponse error = new ErrorResponse(400, message);

        return ResponseEntity.status(400).body(error);
    }
}
