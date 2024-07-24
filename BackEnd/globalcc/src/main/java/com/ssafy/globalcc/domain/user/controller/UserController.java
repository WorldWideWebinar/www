package com.ssafy.globalcc.domain.user.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.user.result.*;
import com.ssafy.globalcc.domain.user.dto.LoginUserDto;
import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.service.UserService;
import com.ssafy.globalcc.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원 가입 api
     * @param userDto : 사용자가 입력한 회원 정보.
     * @return ApiResponse : 저장된 사용자 userId.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto){
        if(!userDto.isIdCheck()) return ResponseEntity.status(400).body("ID 중복 체크를 해주세요.");
        User user = userService.addUser(userDto);
        ApiResponse<UserIdResult> response = ApiResponse.success(new UserIdResult(user.getUserId()),"회원가입 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 중복체크
     * @param id : user ID
     * @return ApiResponse : 중복 여부 (DB에 있을 경우 false, 없을 경우 true)
     */
    @GetMapping("duplication/{id}")
    public ResponseEntity<?> duplication(@PathVariable("id") String id){
        log.debug("check for id : {}",id);

        if(!userService.checkDuplicateUserById(id)){
            return  new ResponseEntity<>(
                    ApiResponse.fail(new DuplicateCheckResult(false),"아이디 중복체크 실패")
                    , HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                ApiResponse.success(new DuplicateCheckResult(true),"아이디 중복체크 성공")
                , HttpStatus.OK
        );
    }

    /**
     * log in user with id and password. check password from DB.
     * if matches, return token.
     * if refresh-token already exists in Redis, new log in will overwrite the existing token.
     * @param userDto : User Dto which has id, plain password.
     * @return : accessToken and refreshToken when success.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto userDto){
        LoginResult result = userService.loginUser(userDto.getId(),userDto.getPassword());
        if(result == null){
            return new ResponseEntity<>(ApiResponse.fail(result,"로그인 실패"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ApiResponse.success(result,"로그인 성공"),HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody UserIdResult userIdResult ){
        if( userService.logoutUser((int) userIdResult.getUserId())){
            return new ResponseEntity<>(ApiResponse.success(userIdResult,"로그아웃 성공"),HttpStatus.OK);
        }
        return new ResponseEntity<>(ApiResponse.fail(userIdResult,"로그아웃 실패"),HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") int userId){
        UserDetailResult result = userService.getUserDetails(userId);
        return new ResponseEntity<>(ApiResponse.success(result,"유저정보 조회 성공"),HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") int userId, @RequestBody UserDto userDto){
        userService.updateUser(userDto,userId);
        return new ResponseEntity<>(ApiResponse.success(new UserIdResult(userId),"유저 수정 성공"),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUser(){
        List<UserListResult> result = userService.getUsers();
        return new ResponseEntity<>(ApiResponse.success(result,"전제 유저 조회 성공"),HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") int userId){
        userService.deleteUser(userId);

        return new ResponseEntity<>(ApiResponse.success(
                new UserIdResult(userId),"유저 삭제 성공"
        ), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@Param("id") int id, @RequestHeader("X-ACCESS-TOKEN") String token){
        String accessToken = userService.getNewAccessTokenWithRefreshToken(id, token);

        if(accessToken == null){
            return new ResponseEntity<>(ApiResponse.fail(null,"리프레시 실패"),HttpStatus.BAD_REQUEST);
        }
        JwtResult result = new JwtResult();
        result.setAccessToken(accessToken);
        result.setRefreshToken(token);
        result.setRefreshTokenExpirationTime(jwtUtil.getTokenExpTime(token));
        return new ResponseEntity<>(ApiResponse.success(result,"리프레시 성공"),HttpStatus.OK);
    }
}
