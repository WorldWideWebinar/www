package com.ssafy.globalcc.domain.user.repository;

import com.ssafy.globalcc.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsById(String id);

    Optional<User> findUserById(String id);
    List<User> findUsersById(List<String> ids);
//    Page<User> findAll(Pageable pageable);


}
