package com.project.community.community_security_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    public String generateToken(String username, boolean mfaVerified) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("mfaVerified", mfaVerified);
        return createToken(claims, username, jwtExpiration);
    }

    public String generateTempToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tempToken", true);
        claims.put("mfaVerified", false);
        return createToken(claims, username, jwtExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject,Long validity) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        System.out.println("secret Key:" +secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean extractMfaVerified(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("mfaVerified", Boolean.class);
    }

    public boolean isTempToken(String token) {
        Claims claims = extractAllClaims(token);
        Boolean isTemp = claims.get("tempToken", Boolean.class);
        return isTemp != null && isTemp;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}