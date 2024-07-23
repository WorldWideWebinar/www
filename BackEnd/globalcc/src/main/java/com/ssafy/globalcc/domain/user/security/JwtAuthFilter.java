package com.ssafy.globalcc.domain.user.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssafy.globalcc.domain.user.service.SecurityUserDetailsService;
import com.ssafy.globalcc.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final SecurityUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    /**
     * JWT 토큰 검증 필터
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO: change header to Authorization for social login
        String authorizationHeader = request.getHeader("X-ACCESS-TOKEN");
        // JWT 헤더 존재
        if (authorizationHeader != null) {
//            String token = authorizationHeader;
            DecodedJWT decoded = jwtUtil.validateToken(authorizationHeader);
            log.debug("has jwt{}", decoded.getToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(decoded.getClaim("userId").asString());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            log.debug(userDetails.getAuthorities().toString());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            log.debug("no jwt");
        }
        filterChain.doFilter(request, response);
    }

}
