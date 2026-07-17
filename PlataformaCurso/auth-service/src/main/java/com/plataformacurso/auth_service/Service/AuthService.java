package com.plataformacurso.auth_service.Service;

import com.plataformacurso.auth_service.Model.AuthResponse;
import com.plataformacurso.auth_service.Model.LoginRequest;
import com.plataformacurso.auth_service.Model.RegisterRequest;
import com.plataformacurso.auth_service.Model.Usuario;
import com.plataformacurso.auth_service.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro para el usuario: {}", request.getEmail());

        // 1. Verificamos si el email ya existe para evitar colisiones en la DB
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya se encuentra registrado");
        }

        // 2. Creamos el usuario encriptando OBLIGATORIAMENTE la contraseña
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario guardado con éxito en la base de datos");

        // 3. Generamos el token JWT para el nuevo usuario
        String jwtToken = jwtService.generateToken(usuario.getEmail(), new HashMap<>());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Iniciando sesión para el usuario: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(usuario.getEmail(), new HashMap<>());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}