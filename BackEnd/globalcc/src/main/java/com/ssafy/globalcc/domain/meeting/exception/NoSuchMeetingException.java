package com.ssafy.globalcc.domain.meeting.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public class NoSuchMeetingException extends NoSuchElementException {
    public NoSuchMeetingException(String message) {
        super(message);
        log.error(message);
    }
}
