package com.ssafy.globalcc.domain.user.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginResult {
    private long userId;
    private JwtResult jwt;
}
