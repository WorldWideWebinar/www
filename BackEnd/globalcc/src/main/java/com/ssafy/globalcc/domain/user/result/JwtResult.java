package com.ssafy.globalcc.domain.user.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtResult {
    private String accessToken;
    private String refreshToken;
    private long refreshTokenExpirationTime;
}
