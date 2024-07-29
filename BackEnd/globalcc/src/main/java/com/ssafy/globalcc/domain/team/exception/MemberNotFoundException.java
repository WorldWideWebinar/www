package com.ssafy.globalcc.domain.team.exception;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException() {
        super("Member not found");
    }
}
