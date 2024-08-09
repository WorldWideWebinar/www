package com.ssafy.globalcc.domain.user.service;

import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.result.LoginResult;
import com.ssafy.globalcc.domain.user.result.UserDetailResult;
import com.ssafy.globalcc.domain.user.result.UserListResult;

import java.util.List;

public interface UserService {
    boolean checkDuplicateUserById(String id);
    LoginResult loginUser(String id, String password);
    boolean logoutUser(int userId);
    UserDetailResult getUserDetails(int userId);
    List<UserListResult> getUsers();
    User addUser(UserDto user);
    void updateUser(UserDto user, int userId);
    void deleteUser(int userId);
    String getNewAccessTokenWithRefreshToken(int id, String refreshToken);
    User findUserById(int userId);
}
