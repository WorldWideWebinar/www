package com.ssafy.globalcc.domain.team.exception;

import java.util.NoSuchElementException;

public class NoSuchTeamException extends NoSuchElementException {

    public NoSuchTeamException() {
        super("팀 정보 확인 실패");
    }
}
