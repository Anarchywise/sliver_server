package com.zh.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private static String SECRET_KEY;
    private static long EXPIRATION_TIME;

    @Value("${custom.jwt.secretKey}")
    public void setSecretKey(String secretKey) {
        JwtUtils.SECRET_KEY = secretKey;
    }

    @Value("${custom.jwt.expirationTime}")
    public void setExpirationTime(long expirationTime) {
        JwtUtils.EXPIRATION_TIME = expirationTime*1000;
    }

    public static String generateJwtToken(String userId) {
        JwtBuilder builder = Jwts.builder()
                .setId(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY);

        // Add custom claims to the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        builder.addClaims(claims);

        return builder.compact();
    }

    public static Claims parseJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static int getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getId());
    }
}