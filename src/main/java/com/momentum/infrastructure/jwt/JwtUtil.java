package com.momentum.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey key;

    public JwtUtil(@Value("${jwt.access-secret}") String secretKey) {
        byte[] decodedKey = Base64.getUrlDecoder().decode(secretKey);  // Base64Url 디코딩
        this.key = Keys.hmacShaKeyFor(decodedKey);  // HS256 알고리즘
    }

    // JWT 검증
    public boolean validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    }

    // Token에서 JWT Claims 추출
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Token에서 User ID 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("user_id", Long.class);
    }
}

