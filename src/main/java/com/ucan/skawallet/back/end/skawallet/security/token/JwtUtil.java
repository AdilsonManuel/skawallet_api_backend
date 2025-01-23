/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil
{

//    @Value("${jwt.secret}")
//    private static final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); // Gera uma chave para HS256
    String secret = Base64.getEncoder().encodeToString(key.getEncoded());

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey()
    {
        // Converte a chave secreta para o formato esperado
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, List<String> roles)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Adiciona as roles no token
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public List<String> extractRoles(String token)
    {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token, String userDetails)
    {
        final String username = extractUsername(token);
        List<String> roles = extractRoles(token);

        return (roles.contains(roles)
                && username.equals(userDetails)
                && !isTokenExpired(token));
    }

    public String extractUsername(String token)
    {
//        System.err.println("JwtUtil.extractUsername()->" + token);
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
