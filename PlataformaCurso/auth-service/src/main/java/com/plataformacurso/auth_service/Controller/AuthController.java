package com.plataformacurso.auth_service.Controller;

import com.plataformacurso.auth_service.Model.AuthResponse;
import com.plataformacurso.auth_service.Model.LoginRequest;
import com.plataformacurso.auth_service.Service.AuthService;
import com.plataformacurso.auth_service.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. INYECTAMOS el servicio de JWT para poder usar la validación abajo
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // 2. CORRECCIÓN: Capturamos el AuthResponse completo
        AuthResponse authResponse = authService.login(loginRequest);

        // Extraemos el string del token que está guardado dentro del objeto AuthResponse
        String token = authResponse.getToken();

        Map<String, String> response = new HashMap<>();
        response.put("Token Generado", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valido", false);
            response.put("mensaje", "Formato de token inválido o ausente");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.substring(7);

        // 3. CORRECCIÓN: Llamamos a jwtService.isTokenValid en vez de authService
        boolean isValid = jwtService.isTokenValid(token);

        if (isValid) {
            response.put("valido", true);
            response.put("mensaje", "El token es completamente válido");
            return ResponseEntity.ok(response);
        } else {
            response.put("valido", false);
            response.put("mensaje", "Token inválido o expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}