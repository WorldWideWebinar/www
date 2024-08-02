package com.ssafy.globalcc.domain.user.result;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserDetailResult {
    String id;
    String name;
    String email;
    String profileImageUrl;
    int totalMeetingTime;
    List<Integer> teamList;

}
