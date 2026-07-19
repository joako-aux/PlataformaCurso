package com.plataformacurso.auth_service.Service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // 1. Inyecta la clave secreta de tu application.properties
    @Value("${jwt.secret}")
    private String secret;

    // 2. Inyecta el tiempo de expiración (3600000 ms = 1 hora)
    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(String username) {
        // Convertimos tu String secreto en una clave segura para HMAC-SHA
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        // Opcional: Aquí puedes meter "Claims" (datos extra que viajarán dentro del token, como roles)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "ROLE_USER"); // Ejemplo

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // El dueño del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Fecha de vencimiento
                .signWith(key, SignatureAlgorithm.HS256) // Firmamos con la llave compartida
                .compact(); // Convierte todo a String
    }
}