package com.pap_shop.util;
import com.pap_shop.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private static final String SECRET_KEY = "fh8d+sYvG+GDpR1ibC0WMsD7q40pTwc4x1d5dTZ1t9nO83ge46CI5JNhbpB9bM9W";

    public static String generateToken(User user) {

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(jti)
                .claim("scope", user.getRole().getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
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