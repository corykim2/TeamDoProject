package com.TeamAA.TeamDo.controller.exceptionhandler;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
