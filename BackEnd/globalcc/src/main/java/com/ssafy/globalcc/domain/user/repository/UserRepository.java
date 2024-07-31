package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUid(String id);
    Optional<User> findUserByUid(String id);
    List<User> findUsersByUidIn(List<String> ids);


}
