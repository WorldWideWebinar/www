package com.ssafy.globalcc.domain.image.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException(String fileName) {
        super(fileName);
    }

}
