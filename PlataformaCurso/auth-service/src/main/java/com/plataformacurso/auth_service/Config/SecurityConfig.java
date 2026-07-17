package com.plataformacurso.auth_service.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter; // El filtro que acabamos de crear

    @Autowired
    private AuthenticationProvider authenticationProvider; // El proveedor que maneja contraseñas/usuarios

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitamos CSRF porque usaremos tokens JWT (no usamos cookies)
                .csrf(csrf -> csrf.disable())

                // 2. Configuramos las reglas de acceso a los endpoints
                .authorizeHttpRequests(auth -> auth
                        // Permitimos que cualquiera entre al login, registro y validación sin token
                        .requestMatchers("/api/auth/**").permitAll()
                        // Cualquier otra petición que no empiece con /api/auth requerirá autenticación obligatoria
                        .anyRequest().authenticated()
                )

                // 3. Indicamos que la sesión no guardará estado (Stateless)
                // Cada petición debe mandar su propio token JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Asignamos el proveedor de autenticación
                .authenticationProvider(authenticationProvider)

                // 5. ¡La magia! Le decimos a Spring que ejecute nuestro JwtAuthenticationFilter
                // justo antes del filtro estándar de usuario y contraseña.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}