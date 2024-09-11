package com.microservice.auth.service.security;

import com.microservice.auth.service.entity.Role;
import com.microservice.auth.service.entity.User;
import com.microservice.commons.dto.RequestDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationTime;
    @Value("${jwt.cookie.name}")
    private String jwtCookie;
    @Value("${jwt.refresh.cookie.name}")
    private String jwtRefreshCookie;

    @Autowired
    private RouteValidator routeValidator;

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = createToken(user);
        return generateCookie(jwtCookie, jwt, "/");
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/");
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/").build();
        return cookie;
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtRefreshCookie, null).path("/").build();
        return cookie;
    }

    public String createToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationTime);
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
            return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, value).path(path).maxAge(4 * 60).httpOnly(true).build();
        return cookie;
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
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

