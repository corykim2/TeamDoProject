package com.TeamAA.TeamDo.controller.exceptionhandler;

public class WithdrawnUserException extends RuntimeException {

    public WithdrawnUserException(String message) {
        super(message);
    }
}
