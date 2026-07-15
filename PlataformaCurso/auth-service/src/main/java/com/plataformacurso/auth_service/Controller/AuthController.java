package com.plataformacurso.auth_service.Controller;

import com.plataformacurso.auth_service.Service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // Endpoint de Registro (Crear usuario y devolver Token)
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {

        // Aquí iría tu lógica para guardar el usuario en la BD con PasswordEncoder

        // Generamos el token JWT para el nuevo usuario
        String token = jwtService.generateToken(username, role);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario registrado exitosamente en QuickBite");
        response.put("username", username);
        response.put("token", token);
        response.put("token_type", "Bearer");

        return ResponseEntity.ok(response);
    }

    // Endpoint de Login normal
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String role) {

        String token = jwtService.generateToken(username, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("token_type", "Bearer");

        return ResponseEntity.ok(response);
    }
}