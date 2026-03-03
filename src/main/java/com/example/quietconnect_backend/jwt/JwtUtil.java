package com.example.quietconnect_backend.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {


    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret){
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Key getKey(){
        return key;
    }

    private final long expiration_time=1000L * 60 * 60 * 24 * 7;

    public String generateToken(String email){
        return Jwts.builder().setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration_time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //it will return the chunk of information
    public Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

    }

    //it will return the respective email since the above function return the respective chunk
    public String extractEmail(String token){
        return extractClaims(token).getSubject().trim().toLowerCase();
    }

    public boolean isValid(String token){
        try {
            extractClaims(token);
            return true;
            
        } catch (Exception e) {
            return false;
        }

    }

    
}
