package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.RefreshToken;
import com.microservice.auth.service.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(User user);
    ResponseCookie generateRefreshJwtCookie(String token);
    String getJwtRefreshFromCookies(HttpServletRequest request);
    ResponseCookie generateJwtCookie(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(Long userId);
}
