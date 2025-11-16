package com.pap_shop.util;
import com.pap_shop.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private static String secretKey;

    @Value("${jwt.secretkey}")
    public void setSecretKey(String key) {
        secretKey = key;
    }

    public static String generateToken(User user) {
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(jti)
                .claim("scope", user.getRole().getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public static Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}