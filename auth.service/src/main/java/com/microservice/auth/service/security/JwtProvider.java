package com.microservice.auth.service.security;

import com.microservice.auth.service.entity.Role;
import com.microservice.auth.service.entity.User;
import com.microservice.commons.dto.RequestDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${time.valid.token}")
    private Long timeValidToken;

    @Autowired
    private RouteValidator routeValidator;

    public String createToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + timeValidToken);
        Map<String, Object> claims = new HashMap<>();
        String rolesString = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
        claims.put("roles", rolesString);
        return Jwts.builder()
                .subject(user.getUserName())
                .claims(claims)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validate(String token, RequestDTO requestDTO) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
        } catch (Exception e) {
            return false;
        }
        if (!isAdmin(token) && routeValidator.isAdminPath(requestDTO)) {
            return false;
        }
        return true;
    }

    public String getUserNameFromToken(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return "Bad token";
        }
    }

    private boolean isAdmin(String token) {
        String roles = (String)Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("roles");
        String[] roleNames = roles.split(",");

        for (String roleName : roleNames) {
            if (roleName.equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }
}

