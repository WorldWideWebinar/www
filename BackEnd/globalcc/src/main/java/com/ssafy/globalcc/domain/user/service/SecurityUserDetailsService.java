package com.ssafy.globalcc.domain.user.service;

import com.ssafy.globalcc.domain.user.entity.User;
import com.ssafy.globalcc.domain.user.repository.UserRepository;
import com.ssafy.globalcc.domain.user.dto.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User dbUser = userRepository.findUserById(id).orElseThrow(() -> new UsernameNotFoundException(id));
        return new SecurityUser(dbUser);
    }
}
