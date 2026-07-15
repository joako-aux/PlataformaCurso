package com.plataformacurso.auth_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // <-- IMPORTANTE: Nueva importación
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // <-- IMPORTANTE: Nueva importación
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ==========================================
    // ¡ESTE ES EL METODO QUE TE FALTABA!
    // Spring Security necesita esto para poder autenticar usuarios
    // ==========================================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs sin estado (Stateless)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitimos acceso libre para pruebas iniciales
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Listo para codificar contraseñas en la base de datos
    }
}