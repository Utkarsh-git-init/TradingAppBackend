package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.entity.UserModel;
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
public class JwtService {

    @Value("${app.jwt-secret-key}")
    private String key;

    public String generate(UserModel user) {
        Map<String,Object> claims=new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 1000L *60*60*24*30))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] byteKey= Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(byteKey);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(    getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validate(String token, UserDetails userDetails) {
        String username=extractUsername(token);
        return username.equals(userDetails.getUsername())&&!isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token,Claims::getExpiration);
    }
}
