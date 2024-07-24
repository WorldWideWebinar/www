package com.ssafy.globalcc.domain.user.result;

import com.ssafy.globalcc.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserListResult {
    Integer userID;
    String id;

    public static void from(User user) {
        
    }
}
