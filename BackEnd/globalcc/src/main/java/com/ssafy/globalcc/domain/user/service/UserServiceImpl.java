package com.ssafy.globalcc.domain.user.service;

import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.entity.UserDetail;
import com.ssafy.globalcc.domain.user.repository.UserDetailRepository;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import com.ssafy.globalcc.domain.user.result.*;
import com.ssafy.globalcc.utils.JwtUtil;
import io.netty.util.internal.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserDetailRepository userDetailRepository;
    private final UserTeamRepository userTeamRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisOperations<String, String> redisOperations;

    @Override
    public boolean checkDuplicateUserById(String id) {
        return userRepository.existsByUid(id);
    }

    @Override
    public LoginResult loginUser(String id, String password) {

        User dbUser = userRepository.findUserByUid(id).orElseThrow(() -> new UsernameNotFoundException("로그인 실패"));
        log.debug("dbUser : {}",dbUser);
        log.debug("password : {}",password);
        if(!passwordEncoder.matches(password, dbUser.getPassword())) {
            return null;
        }
        // id가 없거나, 패스워드를 틀렸으면 null
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
    public UserDetailResult getUserDetails(int userId) {
        UserDetail ud = userDetailRepository.findUserDetailByUserUserId(userId).orElseThrow();
        User user = ud.getUser();
        List<Integer> userTeamList = userTeamRepository.findUserTeamIdsByUserUserId(userId);

        return UserDetailResult.builder()
                .id(user.getUid())
                .name(user.getName())
                .profileImageUrl(user.getProfileImage())
                .email(user.getEmail())
                .totalMeetingTime(ud.getTotalMeetingTime())
                .teamList(userTeamList)
                .build();
    }


    @Override
    public List<UserListResult> getUsers() {
         return userRepository.findAll().stream().map((user)->
             new UserListResult(user.getUserId(),user.getUid())
         ).toList();
    }

    @Override
    public User addUser(UserDto user) {
        String password = user.getPassword();
        password = passwordEncoder.encode(password);
        User dbUser = User.builder()
                .uid(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImageUrl())
                .password(password)
                .language(user.getLanguage())
                .build();
        log.debug("saving User : {}",dbUser);
        dbUser = userRepository.save(dbUser);
        // User Details도 같이 생성.
        userDetailRepository.save(UserDetail.builder()
                .user(dbUser)
                .build()
        );
        return dbUser;
    }

    @Override
    @Transactional
    public void updateUser(UserDto user, int userId) {
        User dbUser = userRepository.findById(userId).orElseThrow();
        // TODO : 비밀번호 변경 로직을 분리
        String encoded = passwordEncoder.encode(user.getPassword());
        dbUser.setPassword(encoded);
        dbUser.setLanguage(user.getLanguage());
        dbUser.setEmail(user.getEmail());
        // 프로필 사진의 경우 다른 곳에서 수정함.

        userRepository.save(dbUser);
    }

    @Override
    public void deleteUser(int userId) {
         // TODO: 정책을 참고해서 유저 삭제 진행
        userRepository.deleteById(userId);
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
