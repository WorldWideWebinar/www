package com.ssafy.globalcc.utils;

import java.time.ZonedDateTime;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssafy.globalcc.domain.user.entity.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
    private final String key;
    private final long accessTokenExpTime;
    @Getter
    private final long refreshTokenExpTime;
    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
                   @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime ){
        this.key = secretKey;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    /**
     * Access Token 생성
     * @param user
     * @return
     */
    public String createAccessToken(User user) {
        return createToken(user, accessTokenExpTime);
    }

    public String createAccessToken(int userId) {
        return createToken(userId, accessTokenExpTime);
    }
    /**
     * Refresh Token 생성
     * @param user
     * @return
     */
    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenExpTime);
    }


    /**
     * JWT 생성
     * @param user
     * @param accessTokenExpTime
     * @return
     */
    private String createToken(User user, long accessTokenExpTime) {
        ZonedDateTime nowDateTime = ZonedDateTime.now();
        ZonedDateTime tokenValidity = nowDateTime.plusSeconds(accessTokenExpTime);
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withIssuer("multiConference")
                .withClaim("userId", user.getId())
                .withIssuedAt(nowDateTime.toInstant())
                .withExpiresAt(tokenValidity.toInstant())
                .sign(algorithm);
    }
    private String createToken(int id, long accessTokenExpTime) {
        ZonedDateTime nowDateTime = ZonedDateTime.now();
        ZonedDateTime tokenValidity = nowDateTime.plusSeconds(accessTokenExpTime);
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withIssuer("multiConference")
                .withClaim("userId", id)
                .withIssuedAt(nowDateTime.toInstant())
                .withExpiresAt(tokenValidity.toInstant())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {

        DecodedJWT decodedJWT = JWT.decode(token);
        try {
            decodedJWT =  JWT.require(Algorithm.HMAC256(key))
                                .build()
                                .verify(token);
            return decodedJWT;
        } catch (AlgorithmMismatchException e) {
            log.info("Algorithm is not matched with HMAC256", e);
        } catch (TokenExpiredException e) {
            log.info("Expired JWT", e);
        } catch (JWTVerificationException e) {
            log.info("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return decodedJWT;
    }

    public long getTokenExpTime(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getExpiresAt().toInstant().getEpochSecond();
    }
}
