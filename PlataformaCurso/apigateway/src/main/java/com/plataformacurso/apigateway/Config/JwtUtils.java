package com.plataformacurso.apigateway.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public void validateToken(final String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        // Si el token es inválido, está expirado o la firma no coincide, lanzará una excepción automáticamente
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    // Opcional: Extraer información si tus otros microservicios la necesitan
    public String extractUsername(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}