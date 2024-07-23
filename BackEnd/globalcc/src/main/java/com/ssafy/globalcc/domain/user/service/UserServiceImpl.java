package com.ssafy.globalcc.domain.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.result.JwtResult;
import com.ssafy.globalcc.domain.user.result.LoginResult;
import com.ssafy.globalcc.domain.user.result.UserDetailResult;
import com.ssafy.globalcc.utils.JwtUtil;
import io.netty.util.internal.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisOperations<String, String> redisOperations;

    @Override
    public boolean checkDuplicateUserById(String id) {
        log.debug("return value of existsById : {}",userRepository.existsById(id));
        return userRepository.existsById(id);
    }

    @Override
    public LoginResult loginUser(String id, String password) {
        User dbUser = userRepository.findUserById(id);
        log.debug("dbUser : {}",dbUser);
        log.debug("password : {}",password);
        // id가 없거나, 패스워드를 틀렸으면 null
        if(dbUser == null || !passwordEncoder.matches(password, dbUser.getPassword())) {
            return null;
        }
        // password correct
        // issue JWT
        String accessToken = jwtUtil.createAccessToken(dbUser);
        String refreshToken = jwtUtil.createRefreshToken(dbUser);
        JwtResult jwtResult = new JwtResult(accessToken,refreshToken,jwtUtil.getRefreshTokenExpTime());
        // save refresh token to redis server
        redisOperations.opsForValue().set(dbUser.getUserId().toString(),refreshToken, jwtUtil.getRefreshTokenExpTime(), TimeUnit.SECONDS);
        return new LoginResult(dbUser.getUserId(),jwtResult);
    }

    @Override
    public boolean logoutUser(int userId) {
        // Redis에서 refresh token 삭제
        var result = redisOperations.delete(Integer.toString(userId));
        if(result != null) {
            return result;
        }
        return false;
    }

    @Override
    public UserDetailResult getUserDetails(long userId) {
        return null;
    }


    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(UserDto user) {
        String password = user.getPassword();
        password = passwordEncoder.encode(password);
        User dbUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(password)
                .language(user.getLanguage())
                .build();
        log.debug("saving User : {}",dbUser);
        return userRepository.save(dbUser);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        userRepository.save(user);
        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        return false;
    }

    @Override
    public String getNewAccessTokenWithRefreshToken(int id, String refreshToken ) {
        // 토큰 검증 (Redis와 비교)
        String redisToken = redisOperations.opsForValue().get(Integer.toString(id));
        if(StringUtil.isNullOrEmpty(redisToken) ||
            !redisToken.equals(refreshToken)) {
            return null;
        }

        // 토큰에서 userID 추출

        return jwtUtil.createAccessToken(id);
    }
}
