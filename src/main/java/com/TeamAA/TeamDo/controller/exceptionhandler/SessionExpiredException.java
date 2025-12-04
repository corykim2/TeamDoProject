package com.TeamAA.TeamDo.controller.exceptionhandler;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
