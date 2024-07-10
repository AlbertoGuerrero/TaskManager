package com.microservice.auth.service.security;

import com.microservice.auth.service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${time.valid.token}")
    private Long timeValidToken;

    public String createToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + timeValidToken);
        return Jwts.builder()
                .subject(user.getUserName())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return "Bad token";
        }
    }
}

