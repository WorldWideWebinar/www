package com.ssafy.globalcc;

import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.repository.TeamRepository;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.repository.UserDetailRepository;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.repository.UserTeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class UserRepoTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserTeamRepository userTeamRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    @Transactional
    public void test() {
//        User user = userRepository.findUserByUserId(5);
//        UserDetail ud = userDetailRepository.findUserDetailByUserUserId(5).orElseThrow();
//        System.out.println(ud);
//        UserDetail ud2 = userDetailRepository.findById(1).orElseThrow();
//        User user = ud2.getUserId();
//        Assertions.assertEquals(user.getId(),"jooo0");

        List<Integer> list = userTeamRepository.findUserTeamIdsByUserUserId(5);
        List<Integer> list2 = userTeamRepository.findUserTeamIdsByUserUserId(6);
//        List<Integer> list2 = userTeamRepository.findUserTeamTeamTeamIdsByUserUserId(5);
        System.out.println(list);
        System.out.println(list2);
    }

    @Test
    @Transactional
    public void paginationTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> pages = userRepository.findAll(pageRequest);

        System.out.println(pages.getContent());


    }

    @Test
    @Transactional
    public void userListTest(){
        Team team = teamRepository.findById(10).orElseThrow(null);
        List<User> users = userTeamRepository.findUserByTeam(team);
        System.out.println(users);
    }

}
