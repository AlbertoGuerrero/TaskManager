package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.RefreshToken;
import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.repository.RefreshTokenRepository;
import com.microservice.auth.service.repository.UserRepository;
import com.microservice.auth.service.security.JwtProvider;
import com.microservice.auth.service.security.exception.TokenRefreshException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jwt.refresh.expiration.time}")
    private Long jwtRefreshExpirationTime;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        if (user == null) {
            return null;
        }
        refreshTokenRepository.deleteByUser(user);
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        existingToken.ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        Date now = new Date();

        refreshToken.setExpiryDate(new Date(now.getTime() + jwtRefreshExpirationTime));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public ResponseCookie generateRefreshJwtCookie(String token) {
        return jwtProvider.generateRefreshJwtCookie(token);
    }

    @Override
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return jwtProvider.getJwtRefreshFromCookies(request);
    }

    @Override
    public ResponseCookie generateJwtCookie(User user) {
        return jwtProvider.generateJwtCookie(user);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        Date now = new Date();
        if (token.getExpiryDate().compareTo(now) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
