package com.ssafy.globalcc.domain.user.exception;

import java.util.NoSuchElementException;

public class NoSuchUserException extends NoSuchElementException {

    public NoSuchUserException(String message) {
        super(message);
    }
}
