package com.ssafy.globalcc.domain.user.service;

import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;

import java.util.List;

public interface UserService {
    boolean checkDuplicateUserById(String id);
    boolean loginUser(String id, String password);
    boolean logoutUser(int userId);
    User getUserById(String id);
    List<User> getUsers();
    User addUser(UserDto user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);

}
