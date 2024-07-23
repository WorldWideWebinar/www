package com.ssafy.globalcc.domain.user.service;

import com.ssafy.globalcc.domain.user.dto.UserDto;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


import java.security.MessageDigest;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean checkDuplicateUserById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean loginUser(String id, String password) {
        User dbUser = userRepository.findUserById(id);
        return false;
    }

    @Override
    public boolean logoutUser(int userId) {
        return false;
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findUserById(id);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(UserDto user) {
        User dbUser = User.builder()
                .email(user.getEmail())
                .language(user.getLanguage())
                .password(user.getPassword())
                .id(user.getId())
                .build();
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
}
