package com.plataformacurso.apigateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class Config {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // Soporta credenciales y patrones de origen modernos
        config.addAllowedMethod("*");        // Permite GET, POST, PUT, DELETE, etc.
        config.addAllowedHeader("*");        // Permite cualquier cabecera (incluyendo Authorization)
        config.setAllowCredentials(true);    // Permite cookies/tokens persistentes si fuesen necesarios

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}