package com.plataformacurso.apigateway.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Puedes agregar propiedades de configuración si las necesitas más adelante
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // 🌟 CLAVE: Si la ruta es pública (/api/auth), dejamos pasar la petición inmediatamente sin validar nada
            if (path.contains("/api/auth/")) {
                return chain.filter(exchange);
            }

            // Aquí va tu lógica actual para rutas protegidas.
            // Si no viene la cabecera Authorization, se bloquea con 403.
            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, HttpStatus.FORBIDDEN);
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.FORBIDDEN);
            }

            // Si pasa las validaciones de token, continúa el flujo normal
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}