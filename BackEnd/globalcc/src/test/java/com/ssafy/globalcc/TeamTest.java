package com.ssafy.globalcc;

import com.ssafy.globalcc.domain.team.dto.TeamDto;
import com.ssafy.globalcc.domain.team.entity.Team;
import com.ssafy.globalcc.domain.team.repository.TeamRepository;
import com.ssafy.globalcc.domain.team.service.TeamService;
import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TeamTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamService teamService;
    @Autowired
    private PayloadRootAnnotationMethodEndpointMapping payloadRootAnnotationMethodEndpointMapping;


    @Test
    public void userListJPATest() {
        List<String> list = new ArrayList<>();
        list.add("jooo0");
        list.add("jooo1");
        List<User> users = userRepository.findUsersByUidIn(list);
        for (User user : users) {
            System.out.println(user.getUid());
        }
    }


    @Test
    public void teamListJPATest() {
        TeamDto dto = new TeamDto(5, "ourTeams", Arrays.asList("jooo1"));
        int teamId = teamService.addTeam(dto);
        System.out.println(teamId);
        assert (teamId == 0);
    }

}
