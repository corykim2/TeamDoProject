package com.TeamAA.TeamDo.controller;

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
}
