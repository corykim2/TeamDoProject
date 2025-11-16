package com.TeamAA.TeamDo.controller.exceptionhandler;

import com.TeamAA.TeamDo.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAuthExceptionHandler {

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
}
