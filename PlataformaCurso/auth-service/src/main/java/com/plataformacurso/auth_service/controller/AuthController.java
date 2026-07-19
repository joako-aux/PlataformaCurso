package com.plataformacurso.auth_service.controller;


import com.plataformacurso.auth_service.dto.AuthRequest;
import com.plataformacurso.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para registrar un nuevo usuario con correo y contraseña.
     * La contraseña se guardará encriptada en la base de datos MySQL.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody AuthRequest request) {
        try {
            String mensaje = authService.registrarUsuario(request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
        } catch (Exception e) {
            // Retorna un error 400 Bad Request si el email ya existe o hay datos inválidos
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint para iniciar sesión.
     * Valida las credenciales contra la base de datos y genera el token JWT seguro.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String token = authService.loginYGenerarToken(request.getEmail(), request.getPassword());

            // Estructuramos la respuesta en un JSON { "token": "valor" }
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            // Retorna 401 Unauthorized si el correo no existe o la contraseña no coincide
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
