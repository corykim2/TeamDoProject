package com.TeamAA.TeamDo.controller.exceptionhandler;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}