package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsById(String id);
    User findUserById(String id);
    List<User> findAllByOrderByIdDesc();
    User findUserByUserId(Integer userId);
    boolean deleteUserByUserId(Integer userId);
}
