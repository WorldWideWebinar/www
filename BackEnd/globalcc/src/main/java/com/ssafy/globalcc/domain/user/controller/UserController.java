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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userService.addUser(userDto);
        ApiResponse<UserIdResult> response = ApiResponse.success(new UserIdResult(user.getUserId()),"회원가입 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 중복체크
     * @param id : user ID
     * @return ApiResponse : 중복 여부 (DB에 있을 경우 false, 없을 경우 true)
     */
    @PostMapping("duplication/{id}")
    public ResponseEntity<?> duplication(@PathVariable("id") String id){
        if(userService.checkDuplicateUserById(id)){

            return ResponseEntity.status(400).body(
                    ApiResponse.fail(new DuplicateCheckResult(false),"아이디 중복체크 실패"));
        }
        return  ResponseEntity.ok(
                ApiResponse.success(new DuplicateCheckResult(true),"아이디 중복체크 성공"));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto userDto){
        if(!userService.loginUser(userDto.getId(),userDto.getPassword())){
            User user = userService.getUserById(userDto.getId());
            String accessToken = jwtUtil.createAccessToken(user);
            String refreshToken = jwtUtil.createRefreshToken(user);
            JwtResult jwtResult = new JwtResult(accessToken,refreshToken,jwtUtil.getRefreshTokenExpTime());
            LoginResult result = new LoginResult(user.getUserId(),jwtResult);
            // TODO : set Redis to save refresh token

            return ResponseEntity.ok(ApiResponse.success(result,"로그인 성공"));
        }
        return ResponseEntity.status(400).body(ApiResponse.fail("","로그인 실패"));
    }


}
