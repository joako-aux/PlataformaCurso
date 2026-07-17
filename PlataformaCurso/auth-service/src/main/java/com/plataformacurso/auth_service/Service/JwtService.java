package com.plataformacurso.auth_service.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private static final String SECRET_KEY = "3cfa76ef14937c1c0ea519f8fc0bed1f0a1e0b5c102a0b3c4d5e6f7g8h9i0j1k";
    private static final long JWT_EXPIRATION = 86400000; // 1 día en milisegundos

    public String generateToken(String email, Map<String, Object> extraClaims) {
        log.info("Generando JWT Token para el usuario: {}", email);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey()) // Sintaxis moderna (detecta algoritmo solo)
                .compact();
    }

    // EXTRAER EL EMAIL DEL USUARIO
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // VALIDAR EL TOKEN COMPLETO
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // VALIDAR SOLO SI NO HA EXPIRADO (Para el endpoint de validación simple)
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // EL MeTODO CON LA SINTAXIS CORREGIDA DE JJWT 0.12+
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}