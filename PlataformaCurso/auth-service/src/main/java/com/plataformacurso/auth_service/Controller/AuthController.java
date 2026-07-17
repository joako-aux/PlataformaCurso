package com.plataformacurso.auth_service.Controller;

import com.plataformacurso.auth_service.Config.JwtUtil;
import com.plataformacurso.auth_service.Model.AuthResponse;
import com.plataformacurso.auth_service.Model.LoginRequest;
import com.plataformacurso.auth_service.Model.RegisterRequest;
import com.plataformacurso.auth_service.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un usuario", description = "Crea una cuenta en BD y retorna el token JWT de inmediato.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("[AUTH SERVICE] Solicitud de registro recibida para: {}", registerRequest.getUsername());

        authService.registrarUsuario(registerRequest);

        String token = jwtUtil.generateToken(registerRequest.getUsername(), registerRequest.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve el Token JWT.")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("[AUTH SERVICE] Intento de login para: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(r -> r.getAuthority())
                    .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(loginRequest.getUsername(), role);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            log.error("[AUTH SERVICE] Error de autenticación para {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Credenciales incorrectas: " + e.getMessage());
        }
    }
}