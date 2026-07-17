package com.plataformacurso.auth_service.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Declaramos el manager como final para asegurar que se inyecte al inicializar
    private final AuthenticationManager authenticationManager;

    // El constructor se encarga de recibir el Bean de SecurityConfig de forma segura
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Aquí va tu lógica para generar el token JWT si la autenticación es exitosa
            return ResponseEntity.ok("Usuario autenticado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas: " + e.getMessage());
        }
    }
}

