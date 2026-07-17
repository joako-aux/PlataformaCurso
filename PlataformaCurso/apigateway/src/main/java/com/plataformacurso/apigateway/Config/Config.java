package com.plataformacurso.apigateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity // Añadimos la anotación de seguridad para WebFlux
public class Config {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // 1. DESHABILITAR CSRF (Esto quitará el 403 de raíz en los métodos POST)
                .csrf(csrf -> csrf.disable())

                // 2. Permitir el paso libre a las rutas de autenticación y Swagger
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // Cualquier otra ruta del gateway requerirá que el usuario esté autenticado
                        .anyExchange().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}