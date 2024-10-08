package com.microservice.auth.service.security;

import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.repository.UserRepository;
import com.microservice.commons.dto.RequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!request.getRequestURI().equals("/auth/refreshtoken") &&
                !request.getRequestURI().equals("/auth/login")) {
            String username = null;
            String jwt = parseJwt(request);
            if (jwt != null) {
                username = jwtProvider.getUserNameFromToken(jwt);
            }
            if (jwt != null && username != null && jwtProvider.validate(jwt, new RequestDTO(request.getRequestURI(), request.getMethod()))) {
                Optional<User> user = userRepository.findByUserName(username);
                if (user.isPresent()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, null);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtProvider.getJwtFromCookies(request);
    }
}